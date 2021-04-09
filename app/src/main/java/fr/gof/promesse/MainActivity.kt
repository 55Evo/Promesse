package fr.gof.promesse

import SwipeToReportOrDone
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import fr.gof.promesse.services.Notifications
import utils.config
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {
    companion object {
        var user = User("a", "Alexislebg", "a", "", Mascot.JACOU)
    }

    lateinit var deleteListener: DeleteButtonListener
    lateinit var recyclerView: RecyclerView
    lateinit var mascotView: ImageView
    val promiseDataBase = PromiseDataBase(this@MainActivity)
    var notifications = Notifications()
    lateinit var slidr: SlidrInterface

    lateinit var adapter: PromiseAdapter
    lateinit var listPromesse: TreeSet<Promise>
    lateinit var layout: ConstraintLayout
    var dateOfTheDay: Date? = null

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var del: FloatingActionButton

    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slidr = Slidr.attach(this, config);
        setContentView(R.layout.activity_main)
        dateOfTheDay = Date(System.currentTimeMillis())
        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        layout = findViewById(R.id.ConstraintLayout)
        user.loadPromises(promiseDataBase)
        listPromesse = user.getAllPromisesOfTheDay()
        adapter = PromiseAdapter(
            listPromesse,
            PromiseEventListener(listPromesse, this),
            this,
            false
        )

        del = findViewById(R.id.deleteButton)
        threadLinkBackground()
        recyclerView.adapter = adapter
        deleteListener = DeleteButtonListener(adapter, this)
        del.setOnClickListener(deleteListener)
        enableSwipeToDoneOrReport()
        notifications.scheduleJob(this, user)
        user.mascot.mascotWelcomeMessage(
            this,
            listPromesse,
            findViewById(R.id.mascotBubbleTextView)
        )
    }

    /**
     * On resume called when activity is called again.
     * It refresh the view.
     *
     * Méthode appelée quand une activité est ouverte de nouveau.
     * Elle permet de mettre à jour la vue.
     *
     */
    override fun onResume() {
        super.onResume()
        mascotView = findViewById(R.id.imageViewMascot)
        mascotView.setImageResource(user.mascot.image)
        listPromesse = user.getAllPromisesOfTheDay()
        adapter = PromiseAdapter(
            listPromesse,
            PromiseEventListener(listPromesse, this),
            this,
            false
        )
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /**
     * Thread to link the background and refresh the date every second.
     *
     * Méthod qui permet de créer un thread qui actualise l'arrière-plan
     * en fonction de l'heure et la date toutes les secondes.
     *
     */
    private fun threadLinkBackground() {
        mHandler = Handler()
        mRunnable = Runnable {
            linkBackground()
            updateDate()
            mHandler.postDelayed(mRunnable, 1000)
        }
        mRunnable.run()
    }

    /**
     * Update date on the main activity.
     *
     * Met à jour la date de l'activité principale.
     *
     */
    private fun updateDate() {
        var date = findViewById<TextView>(R.id.dateDayView)
        val dt = Date()
        val dfs = DateFormatSymbols(Locale.FRANCE)
        val dateFormat = SimpleDateFormat("EEEE dd MMMM", dfs)
        val date1 = dateFormat.format(dt)
        var res = ""
        val formatter = SimpleDateFormat("YYYY")
        val date2 = formatter.format(Date())
        res += date2
        res += "\n" + date1.substring(0, 1).toUpperCase() + date1.substring(1).toLowerCase();
        date.text = res
    }

    /**
     * Lock slider method that lock the back slide
     *
     * Méthode qui permet de bloquer le retour arrière
     * via le slide
     *
     */
    private fun lockSlider() {
        slidr.lock()
    }

    /**
     * Un lock slider method that unlock the back slide
     *
     * Méthode qui permet de débloquer le retour arrière
     * via le slide
     *
     */
    private fun unLockSlider() {
        slidr.unlock()
    }

    /**
     * Enable swipe to done or report that enable the swipe of promise.
     *
     * Permet d'activer le swipe d'une promesse.
     *
     */
    private fun enableSwipeToDoneOrReport() {
        var idNotification = -1L

        val swipeToReportOrDone: SwipeToReportOrDone = object : SwipeToReportOrDone(this) {
            /**
             * On swiped called when user swipe a promise
             * to the left or the right side.
             * Left : the promise is done.
             * Right : the promise is reported to tomorrow.
             *
             * @param viewHolder
             * @param i
             *
             * Méthode appelée lorsque l'utilisateur glisse une promesse
             * à droite ou à gauche.
             * S'il la glisse à gauche, elle est terminée et passe en done.
             * S'il la glisse à droite, elle est reportée au lendemain.
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                var promise = listPromesse.elementAt(position)
                var date = promise.dateTodo
                var message = ""

                when (i) { // promise done
                    utils.LEFT -> {
                        message = getString(R.string.promiseDone)
                        promise.state = State.DONE
                        user.updatePromise(promise)
                        idNotification = user.unreadNotification(promise)
                        user.stopDnd(this@MainActivity)
                    }
                    utils.RIGHT -> { // add 1 day to the date to do to postpone it
                        message = getString(R.string.promisePostponed)
                        promise.dateTodo = Date(System.currentTimeMillis() + 86400000)
                        promise.state = State.TODO
                        user.updatePromise(promise)
                        user.stopDnd(this@MainActivity)
                    }
                }
                if (adapter.inSelection) {
                    for (l in adapter.promiseList) {
                        l.isChecked = false
                    }
                    adapter.nbPromisesChecked = 0
                    adapter.inSelection = false
                    var bundle = Bundle()
                    bundle.putBoolean("longclick", true)
                    for (i in 0..(adapter.promiseList.size)) {
                        adapter.notifyItemChanged(i, bundle);
                    }
                    adapter.showOffDdelete()
                }
                listPromesse.remove(listPromesse.elementAt(position))
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, listPromesse.size)
                snackbarUndo(message, i, promise, date, position)
            }

            /**
             * On move is called when user drag a promise from top to bottom or from bottom to top.
             * It disable the deleteMode that is activated by a longClick on promise and
             * organize the promises and update the view.
             *
             * @param recyclerView
             * @param source
             * @param target
             * @return true
             *
             * Méthode qui est appelée quand un utilisateur drag une promesse de haut en bas
             * ou de bas en haut.
             * Elle désactive le mode suppression qui s'active par un clic long sur une promesse
             * et réorganise les promesses pour ensuite mettre à jour la vue.
             */
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (source.itemViewType != target.itemViewType) {
                    return false
                }
                adapter.nbPromisesChecked = 0
                adapter.inSelection = false
                adapter.promiseList.elementAt(source.adapterPosition).isChecked = false
                var bundle = Bundle()
                bundle.putBoolean("longclick", true)
                for (i in 0..adapter.promiseList.size)
                    adapter.notifyItemChanged(i, bundle)
                adapter.showOffDdelete()
                adapter.onItemMove(source.adapterPosition, target.adapterPosition)
                return true
            }

            /**
             * Snackbar undo called when user click on undoButton after a swipe.
             * It restores the old state of the promise.
             *
             * @param message
             * @param i
             * @param promise
             * @param date
             * @param position
             *
             * Méthode appelée quand l'utilisateur appuie sur le bouton annuler
             * après avoir swipe une promesse.
             * Elle remet la promesse à son ancien état.
             */
            private fun snackbarUndo(
                message: String,
                i: Int,
                promise: Promise,
                date: Date,
                position: Int
            ) {
                val snackbar = Snackbar
                    .make(layout, message, Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.cancel)) {
                    if (i == utils.RIGHT) {
                        promise.dateTodo = date
                        adapter.restoreItem(promise, position, promiseDataBase)
                    } else if (i == utils.LEFT) {
                        // j'enlève le done
                        adapter.restoreItem(promise, position, promiseDataBase)
                        promise.state = State.TODO
                        user.updatePromise(promise)
                        user.removeNotification(idNotification)
                    }
                    recyclerView.scrollToPosition(position)
                    adapter.notifyItemRangeChanged(position, listPromesse.size)
                }
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show()
            }
        }
        val itemReport = ItemTouchHelper(swipeToReportOrDone)
        itemReport.attachToRecyclerView(recyclerView)
    }

    /**
     * Link the background to the time of day.
     *
     * Permet de lier l'arrière-plan à l'heure du jour.
     *
     */
    private fun linkBackground() {
        val constraintLayout: ConstraintLayout = findViewById(R.id.ConstraintLayout)
        val now = Calendar.getInstance()
        if (now.get(Calendar.HOUR_OF_DAY) in 6..20) {
            constraintLayout.background = getDrawable(R.drawable.day)
        } else {
            constraintLayout.background = getDrawable(R.drawable.night)
        }
    }

    /**
     * On add button clicked called when addButton is clicked.
     * It starts promiseManagerActivity.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur clique sur le bouton
     * d'ajout d'une promesse.
     * Elle ouvre l'activité promiseManager.
     */
    fun onAddButtonClicked(v: View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

    /**
     * On click mascot called when user click on the mascot.
     * It displays a message in a bubble that the mascot says.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur clique sur la mascotte.
     * Permet de faire parler la mascotte en affichant un message
     * dans une bulle à côté d'elle.
     */
    fun onClickMascot(v: View) {
        user.mascot.displayMascotMessage(
            String.format(getString(R.string.clicMessageMascot), user.mascot.nom),
            findViewById(R.id.mascotBubbleTextView),
            this
        )
    }

    /**
     * On click search button called when searchButton is clicked.
     * It open the searchActivity.
     *
     * @param v
     *
     * Méthode appelée quand l'utilisateur appuie sur le bouton de recherche.
     * Elle ouvre l'activité de recherche.
     */
    fun onClickSearchButton(v: View) {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    /**
     * On click profile button called when the profileButton is clicked.
     * It starts profileActivity.
     *
     * @param v
     *
     * Méthode appelée quand l'utilisateur clique sur le bouton de profile.
     * Elle ouvre l'activité du profile utilisateur.
     */
    fun onClickProfileButton(v: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    /**
     * Is done that set a promise to done.
     *
     * @param p
     * @param a
     *
     * Méthode qui met une promesse à l'état de terminée.
     */
    fun isDone(p: Promise, a: PromiseAdapter) {
        user.setToDone(p)
        a.notifyDataSetChanged()
    }

    /**
     * On click calendar button called when the calendarButton is clicked.
     * It starts calendarActivity.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur clique sur le bouton calendrier.
     * Elle ouvre l'activité du calendrier.
     */
    fun onClickCalendarButton(v: View) {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }

    /**
     * On destroy called when the activity is finished.
     * It destroys the thread that updates the background.
     *
     * Méthode appelée quand l'activité est terminée.
     * Permet d'arrêter le thread qui lie le background
     * à l'heure du jour afin qu'il ne tourne pas en arrière-plan.
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mRunnable)
    }
}
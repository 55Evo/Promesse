package fr.gof.promesse

import SwipeToReportOrDone
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
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
        var user = User("a","Alexislebg", "a", "", Mascot.JACOU)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slidr = Slidr.attach(this, config);
        setContentView(R.layout.activity_main)
        dateOfTheDay = Date(System.currentTimeMillis())
        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mascotView = findViewById(R.id.imageViewMascot)
        mascotView.setImageResource(user.mascot.image)
        recyclerView.layoutManager = llm
        layout = findViewById(R.id.ConstraintLayout)
        user.loadPromises(promiseDataBase)
        listPromesse = user.getAllPromisesOfTheDay()
        adapter = PromiseAdapter(listPromesse,
                PromiseEventListener(listPromesse, this),
                this,
                false)

        del = findViewById(R.id.deleteButton)
        threadLinkBackground()
        recyclerView.adapter = adapter
        deleteListener = DeleteButtonListener(adapter, this)
        del.setOnClickListener(deleteListener)
        enableSwipeToDoneOrReport()
        notifications.scheduleJob(this, user)
        val now = Calendar.getInstance()
        if (now.get(Calendar.HOUR_OF_DAY) in 6..20) {
            displayMascotMessage(getString(R.string.dayMascotMessage))
        } else {
            displayMascotMessage(getString(R.string.nightMascotMessage))
        }
    }

    /**
     * Thread to link the background and refresh the date every second
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
     * Update date on the main activity
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
     * Lock slider
     *
     */
    private fun lockSlider() {
        slidr.lock()
    }

    /**
     * Un lock slider
     *
     */
    private fun unLockSlider() {
        slidr.unlock()
    }

    /**
     * Enable swipe to done or report
     *
     */
    private fun enableSwipeToDoneOrReport() {
        var idNotification = -1L

        val swipeToReportOrDone: SwipeToReportOrDone = object : SwipeToReportOrDone(this) {
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
             * On move
             *
             * @param recyclerView
             * @param source
             * @param target
             * @return
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
             * Snackbar undo
             *
             * @param message
             * @param i
             * @param promise
             * @param date
             * @param position
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
                snackbar.show();
               //
            }
        }
        val itemReport = ItemTouchHelper(swipeToReportOrDone)
        itemReport.attachToRecyclerView(recyclerView)
    }


    override fun onResume() {
        super.onResume()
        listPromesse = user.getAllPromisesOfTheDay()
        adapter = PromiseAdapter(listPromesse,
                PromiseEventListener(listPromesse, this),
                this,
                false)
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /**
     * Link the background to the time of day
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
     * On add button clicked
     *
     * @param v
     */
    fun onAddButtonClicked(v: View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

    /**
     * Display mascot message
     *
     * @param message to display
     */
    private fun displayMascotMessage(message: String){
        var bubble: TextView = findViewById(R.id.mascotBubbleTextView)
        bubble.text = message
        bubble.visibility = View.VISIBLE
        Handler().postDelayed({
            bubble.visibility = View.GONE
        }, 5000)
    }

    /**
     * On click mascot
     *
     * @param v
     */
    fun onClickMascot(v: View) {
        displayMascotMessage("Coucou c'est moi ${user.mascot.nom} !")
    }

    /**
     * On click search button
     *
     * @param v
     */
    fun onClickSearchButton(v: View) {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    fun isDone(p: Promise, a: PromiseAdapter) {
        user.setToDone(p)
        a.notifyDataSetChanged()
    }

    /**
     * On click calendar button
     *
     * @param v
     */
    fun onClickCalendarButton(v: View) {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mRunnable)
    }
}
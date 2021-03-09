package fr.gof.promesse

import SwipeToReportOrDone
import SwipeupDown
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*

import fr.gof.promesse.services.Notifications

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {
    companion object {
        var user = User("a", "a", "", Mascot.JACOU)
    }

    lateinit var deleteListener: DeleteButtonListener
    lateinit var recyclerView: RecyclerView
    lateinit var mascotView : ImageView
    val promiseDataBase = PromiseDataBase(this@MainActivity)
    var notifications = Notifications()

    lateinit var adapter : PromiseAdapter
    lateinit var listPromesse : MutableList<Promise>
    lateinit var layout : ConstraintLayout
    var dateOfTheDay : Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                dateOfTheDay = extras.getSerializable("dateOfTheDayRequested") as Date
            }
        } else {
            dateOfTheDay = savedInstanceState.getSerializable("dateOfTheDayRequested") as Date
        }
        if(dateOfTheDay == null) dateOfTheDay = Date(System.currentTimeMillis())

        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mascotView = findViewById(R.id.imageViewMascot)
        mascotView.setImageResource(user.mascot.image)
        recyclerView.layoutManager = llm
        layout = findViewById(R.id.ConstraintLayout)
        user.loadPromises( promiseDataBase)
        listPromesse = user.getAllPromisesOfTheDay(promiseDataBase).toMutableList()

        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this), this)

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        recyclerView.adapter = adapter
        deleteListener = DeleteButtonListener(adapter, this, promiseDataBase)
        del.setOnClickListener(deleteListener)
        enableSwipeToDoneOrReport()
        //enableSwipeUpDown()
        notifications.scheduleJob(this, user)
    }
    private fun enableSwipeUpDown(){
        val swipeupDown: SwipeupDown = object : SwipeupDown(this) {
            override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                if (source.itemViewType != target.itemViewType) {
                    return false
                }
                // Notify the adapter of the move
                adapter.onItemMove(source.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }
        val itemReport = ItemTouchHelper(swipeupDown)
        itemReport.attachToRecyclerView(recyclerView)
    }

    private fun enableSwipeToDoneOrReport(){
        val swipeToReportOrDone: SwipeToReportOrDone = object : SwipeToReportOrDone(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                var promise = listPromesse[position]
                var date = promise.dateTodo
                var message = ""
                when(i){ // promise done
                    16 -> {
                        message = getString(R.string.promiseDone)
                        promise.state = State.DONE
                        user.updatePromise(promise, promiseDataBase)
                    }
                    32 -> { // add 1 day to the date to do to postpone it
                        message = getString(R.string.promisePostponed)
                        promise.dateTodo = Date(System.currentTimeMillis() + 86400000)
                        user.updatePromiseDate(promise, promiseDataBase)

                    }
                }
                listPromesse.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, listPromesse.size)
                snackbarUndo(message, i, promise, date, position)

            }

            private fun snackbarUndo(message: String, i: Int, promise: Promise, date: Date, position: Int) {
                val snackbar = Snackbar
                        .make(layout, message, Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.cancel)) {
                    if (i == 32) {
                        promise.dateTodo = date
                        adapter.restoreItem(promise, position, promiseDataBase)
                    } else {
                        // j'enlève le done
                        adapter.restoreItem(promise, position, promiseDataBase)
                        promise.state = State.TODO
                        user.updatePromise(promise, promiseDataBase)
                    }
                    recyclerView.scrollToPosition(position)
                    adapter.notifyItemRangeChanged(position, listPromesse.size)
                }
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();
            }

        }
        val itemReport = ItemTouchHelper(swipeToReportOrDone)
        itemReport.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
//        listPromesse = user.getAllPromisesOfTheDay(promiseDataBase, dateOfTheDay!!).toMutableList()
        listPromesse = user.getAllPromisesOfTheDay(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this), this)
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
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
     * On click mascot
     *
     * @param v
     */
    fun onClickMascot(v: View){
       var bubble : TextView = findViewById(R.id.mascotBubbleTextView)
        bubble.text = "Coucou c'est moi "+user.mascot.nom + " !"
        bubble.visibility = View.VISIBLE
        Handler().postDelayed({
            bubble.visibility = View.GONE
        }, 5000)
    }

    /**
     * On click search button
     *
     * @param v
     */
    fun onClickSearchButton(v: View){
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    fun isDone(p : Promise, a : PromiseAdapter) {
        user.setToDone(p, promiseDataBase)
        a.notifyDataSetChanged()
    }

    fun onClickCalendarButton(v: View){
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}
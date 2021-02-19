package fr.gof.promesse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.model.Promise
import fr.gof.promesse.services.Notifications

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {

    lateinit var deleteListener: DeleteButtonListener
    lateinit var recyclerView: RecyclerView
    lateinit var mascotView : ImageView
    val promiseDataBase = PromiseDataBase(this@MainActivity)
    var notifications = Notifications()

    lateinit var adapter : PromiseAdapter
    lateinit var listPromesse : MutableList<Promise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notifications.scheduleJob(this)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mascotView = findViewById(R.id.imageViewMascot)
        mascotView.setImageResource(utils.user.mascot.image)
        recyclerView.layoutManager = llm

        listPromesse = utils.user.getAllPromisesOfTheDay(promiseDataBase).toMutableList()

        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        recyclerView.adapter = adapter
        deleteListener = DeleteButtonListener(adapter, this, promiseDataBase)
        del.setOnClickListener(deleteListener)
//        var notif = Notifications()
//        if(listPromesse.size > 0)
//            notif.createNotification(listPromesse[0], this)
    }

    override fun onResume() {
        super.onResume()
        listPromesse = utils.user.getAllPromisesOfTheDay(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))
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
        bubble.text = "Coucou c'est moi "+utils.user.mascot.name + " !"
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

}
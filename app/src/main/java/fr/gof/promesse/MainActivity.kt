package fr.gof.promesse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var deleteListener: DeleteButtonListener
    lateinit var recyclerView: RecyclerView
    lateinit var mascotView : ImageView
    val promiseDataBase = PromiseDataBase(this@MainActivity)

    lateinit var adapter : PromiseAdapter
    lateinit var listPromesse : MutableList<Promise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mascotView = findViewById(R.id.imageViewMascot)
        mascotView.setImageResource(utils.user.mascot.image)
        recyclerView.layoutManager = llm

        listPromesse = utils.user.getAllPromise(promiseDataBase).toMutableList()

        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        recyclerView.adapter = adapter
        deleteListener = DeleteButtonListener(adapter, this, promiseDataBase)
        del.setOnClickListener(deleteListener)

    }

    override fun onResume() {
        super.onResume()
        println("resume onResume MainActivity")
        listPromesse = utils.user.getAllPromise(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun onAddButtonClicked (v : View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }
    fun onClickMascot(v:View){
        var bubble : TextView = findViewById(R.id.mascotBubbleTextView)
        bubble.text = "Coucou c'est moi "+utils.user.mascot.name + " !"
        bubble.visibility = View.VISIBLE
        Handler().postDelayed({
            bubble.visibility = View.GONE
        }, 5000)


    }
    fun onClickSearchButton(v : View){
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

}
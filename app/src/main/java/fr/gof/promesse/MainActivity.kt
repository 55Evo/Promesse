package fr.gof.promesse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    val promiseDataBase = PromiseDataBase(this@MainActivity)

    lateinit var adapter : PromiseAdapter
    lateinit var defaultUser : User
    lateinit var listPromesse : MutableList<Promise>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        defaultUser = promiseDataBase.createDefaultAccount()
        for(i in 0..2){
            var promesse = Promise(-1, "promesse numero $i", 5, State.DONE, false, "description numero $i blablablablablablablablablablablablablablablablablabalblabkablababbjbfjksdbfhjdgbfjhsbvfhjsdvfhjsqdhjqvhsvfdsf", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
            defaultUser.addPromise(promesse, promiseDataBase)
        }

        for(i in 0..2){
            var promesse = Promise(-1, "promesse priorite numero $i", 5, State.DONE, true, "description priorité numero $i blablablablablablablablablablablablablablablablablabalblabkablababbjbfjksdbfhjdgbfjhsbvfhjsdvfhjsqdhjqvhsvfdsf", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
            defaultUser.addPromise(promesse, promiseDataBase)
        }

        listPromesse = defaultUser.getAllPromise(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        recyclerView.adapter = adapter
        del.setOnClickListener(DeleteButtonListener(adapter, listPromesse, this))

    }

    override fun onResume() {
        super.onResume()
        println("resume onResume MainActivity")
        listPromesse = defaultUser.getAllPromise(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun onAddButtonClicked (v : View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }


}
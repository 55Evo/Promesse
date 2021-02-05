package fr.gof.promesse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    val promiseDataBase = PromiseDataBase(this@MainActivity)

    lateinit var adapter : PromiseAdapter
    lateinit var defaultUser : User
    lateinit var listPromesse : MutableList<Promise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPromesse)
        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        utils.user = promiseDataBase.createDefaultAccount(Mascot("Super mascotte",R.drawable.mascot1))
        for(i in 0..20){
            var promesse = Promise(-1, "promesse numero $i", 5, State.DONE, false, "description numero $i blablablablablablablablablablablablablablablablablabalblabkablababbjbfjksdbfhjdgbfjhsbvfhjsdvfhjsqdhjqvhsvfdsf", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
            utils.user.addPromise(promesse, promiseDataBase)
        }

        for(i in 0..20){
            var promesse = Promise(-1, "promesse priorite numero $i", 5, State.DONE, true, "description priorité numero $i blablablablablablablablablablablablablablablablablabalblabkablababbjbfjksdbfhjdgbfjhsbvfhjsdvfhjsqdhjqvhsvfdsf", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
            utils.user.addPromise(promesse, promiseDataBase)
        }

        listPromesse = utils.user.getAllPromise(promiseDataBase).toMutableList()

        adapter = PromiseAdapter(listPromesse, PromiseEventListener(listPromesse, this))

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        recyclerView.adapter = adapter
        del.setOnClickListener(DeleteButtonListener(adapter, listPromesse, this))

    }


}
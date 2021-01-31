package fr.gof.promesse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@MainActivity)

    lateinit var defaultUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultUser = promiseDataBase.createDefaultAccount()
        //val label = findViewById<TextView>(R.id.test)
        val promesse = Promise(-1, "Titre", 5, State.TODO, true, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        val promesse2 = Promise(0, "Titre2", 5, State.TODO, true, "Desc2", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)

        defaultUser.addPromise(promesse, promiseDataBase)
        /*println(defaultUser.getAllPromise(promiseDataBase).toString())
        label.setText(defaultUser.getAllPromise(promiseDataBase).toString())*/

        val setPromesse = promiseDataBase.getAllPromises(defaultUser.email)
        val listPromesse = mutableListOf<Promise>(promesse, promesse2)
        var arrayPromesse = Array<String>(listPromesse.size, {i-> ""})
        //for((i, promise) in listPromesse.withIndex()) {
          //       arrayPromesse[i]= ("${promise.title}\n${promise.description}")
        //}

        val adapter = ArrayAdapter(this, R.layout.listitem_view_promesse, listPromesse)

        val listView: ListView = findViewById(R.id.listViewPromesse)
        listView.adapter = adapter
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, _, id ->
            listPromesse.removeAt(id.toInt())
            
            adapter.notifyDataSetChanged()
            true}
    }
}
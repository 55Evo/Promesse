package fr.gof.promesse

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.marginTop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.Subtask
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
        val promesse2 = Promise(-1, "Titre2", 5, State.TODO, true, "Desc2", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        val promesse3 = Promise(-1, "Titre3", 5, State.TODO, false, "Desc3", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        val promesse4 = Promise(-1, "Titre4", 5, State.TODO, false, "Descr4", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), mutableListOf(Subtask(0, "Sstache", false)))
        //defaultUser.addPromise(promesse, promiseDataBase)
        //defaultUser.addPromise(promesse2, promiseDataBase)
        //defaultUser.addPromise(promesse3, promiseDataBase)
        //defaultUser.addPromise(promesse4, promiseDataBase)
        /*println(defaultUser.getAllPromise(promiseDataBase).toString())
        label.setText(defaultUser.getAllPromise(promiseDataBase).toString())*/

        val setPromesse = defaultUser.getAllPromise(promiseDataBase)
        //val listPromesse = mutableListOf<Promise>(promesse, promesse2)
        val listPromesse = setPromesse.toMutableList()
        val listPromesseDescr = mutableListOf<String>()
        for(promise in listPromesse) {
          var s = if(promise.priority) { "★ " } else { "" } + "${promise.title}\n${promise.description}" + if (promise.subtasks != null) { "\nSous-tâches !" } else { "" }

          listPromesseDescr.add(s)
        }

        val adapter = ArrayAdapter(this, R.layout.listitem_view_promesse, listPromesseDescr)
        val del = findViewById<FloatingActionButton>(R.id.deleteButton)
        val listView: ListView = findViewById(R.id.listViewPromesse)
        listView.adapter = adapter
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, _, id ->

            del.visibility = View.VISIBLE
            del.translationY = listView.marginTop.toFloat() + (view.height/10) + (id*view.height)
            del.setOnClickListener(View.OnClickListener { view ->
                if (listPromesse[id.toInt()].subtasks != null) {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("Attention, cette promesse possède des sous-tâches. " +
                            "Êtes-vous sûr(e) de vouloir la supprimer ?")
                            .setCancelable(true)
                            .setPositiveButton("Oui", DialogInterface.OnClickListener {
                                _, _ -> promiseDataBase.deletePromise(listPromesse.get(id.toInt()))
                                listPromesse.removeAt(id.toInt())
                                listPromesseDescr.removeAt(id.toInt())
                                adapter.notifyDataSetChanged()
                                del.visibility = View.INVISIBLE
                            })
                            .setNegativeButton("Non", DialogInterface.OnClickListener {
                                dialog, _ -> dialog.cancel()
                            })

                    val alert = dialogBuilder.create()
                    alert.setTitle("Suppression de promesse")
                    alert.show()

                }
                else {
                    promiseDataBase.deletePromise(listPromesse.get(id.toInt()))
                    listPromesse.removeAt(id.toInt())
                    listPromesseDescr.removeAt(id.toInt())
                    adapter.notifyDataSetChanged()
                    del.visibility = View.INVISIBLE
                }
            })

            true}
    }
}
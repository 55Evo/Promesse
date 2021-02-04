package fr.gof.promesse

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.Adapter.SearchAdapter
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
        val listView: RecyclerView = findViewById(R.id.listViewPromesse)
        listView.layoutManager = LinearLayoutManager(this)
        listView.setHasFixedSize(true)

        defaultUser = promiseDataBase.createDefaultAccount()
        //val label = findViewById<TextView>(R.id.test)
        val promesse4 = Promise(-1, "Titre4", 5, State.TODO, false, "Descr4", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), mutableListOf(Subtask(0, "Sstache", false)))
        //defaultUser.addPromise(promesse, promiseDataBase)
        //defaultUser.addPromise(promesse2, promiseDataBase)
        //defaultUser.addPromise(promesse3, promiseDataBase)
        defaultUser.addPromise(promesse4, promiseDataBase)
        /*println(defaultUser.getAllPromise(promiseDataBase).toString())
        label.setText(defaultUser.getAllPromise(promiseDataBase).toString())*/

        val promesse = Promise(-1, "faire l'amour", 5, State.DONE, true, "Ceci est la description de ce que faire l'amour signifie, \n cela signifie que pour réussir il faut aimer et pour aimer il faut avoir ", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promesse, promiseDataBase)
        val promessee = Promise(-1, "passer le permis", 5, State.DONE, true, "avoir le permis quelle belle ambition mais on ne sait pas si elle se réalisera un jour tellement tu n'es pas doué mon pauvre... on va quand meme essayer meme si cela sera dur", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promessee, promiseDataBase)
        val promesse1 = Promise(-1, "faire dodo", 5, State.TODO, false, "Dormir un bien grand mot bien plus grand que le mot sage par exemple", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse1, promiseDataBase)
        val promesse2 = Promise(-1, "faire des enfants", 5, State.TODO, false, "avoir des enfants s'en occuper.. Cela nest pas donne a tout le monde beaucoup les abandonnent", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis() + 200), null)
        defaultUser.addPromise(promesse2, promiseDataBase)
        val promesse3 = Promise(-1, "faire des études", 5, State.TODO, true, "faire des etudes je ne connais pas j'ai toujours ete chaumeur", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse3, promiseDataBase)

        val setPromesse = defaultUser.getAllPromise(promiseDataBase)
        //val listPromesse = mutableListOf<Promise>(promesse, promesse2)
        val listPromesse = setPromesse.toMutableList()
        val listPromesseDescr = mutableListOf<String>()
        for(promise in listPromesse) {
          var s = if(promise.priority) { "★ " } else { "" } + "${promise.title}\n${promise.description}" + if (promise.subtasks != null) { "\nSous-tâches !" } else { "" }

          listPromesseDescr.add(s)
        }
        println(listPromesse)
        //val adapter = ArrayAdapter(this, R.layout.layoutsearchitems, listPromesseDescr)
        val adapter = SearchAdapter(this, listPromesse)

        val del = findViewById<FloatingActionButton>(R.id.deleteButton)


        listView.adapter = adapter
//        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, _, id ->
//
//            del.visibility = View.VISIBLE
//            del.translationY = listView.marginTop.toFloat() + (view.height/10) + (id*view.height)
//            del.setOnClickListener(View.OnClickListener { view ->
//                if (listPromesse[id.toInt()].subtasks != null) {
//                    val dialogBuilder = AlertDialog.Builder(this)
//                    dialogBuilder.setMessage("Attention, cette promesse possède des sous-tâches. " +
//                            "Êtes-vous sûr(e) de vouloir la supprimer ?")
//                            .setCancelable(true)
//                            .setPositiveButton("Oui", DialogInterface.OnClickListener {
//                                _, _ -> promiseDataBase.deletePromise(listPromesse.get(id.toInt()))
//                                listPromesse.removeAt(id.toInt())
//                                listPromesseDescr.removeAt(id.toInt())
//                                adapter.notifyDataSetChanged()
//                                del.visibility = View.INVISIBLE
//                            })
//                            .setNegativeButton("Non", DialogInterface.OnClickListener {
//                                dialog, _ -> dialog.cancel()
//                            })
//
//                    val alert = dialogBuilder.create()
//                    alert.setTitle("Suppression de promesse")
//                    alert.show()
//
//                }
//                else {
//                    promiseDataBase.deletePromise(listPromesse.get(id.toInt()))
//                    listPromesse.removeAt(id.toInt())
//                    listPromesseDescr.removeAt(id.toInt())
//                    adapter.notifyDataSetChanged()
//                    del.visibility = View.INVISIBLE
//                }
//            })
//
//            true}
    }
}
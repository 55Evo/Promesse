package fr.gof.promesse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.Adapter.MascotAdapter
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.MascotListener
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User


class ChooseMascotActivity : AppCompatActivity() {
    val promiseDataBase = PromiseDataBase(this)

    lateinit var adapter : MascotAdapter
    lateinit var defaultUser : User
    lateinit var recyclerView: RecyclerView
    private val listMascot: List<Mascot> = listOf(
        Mascot("Jacou le Hibou", R.drawable.mascot1, R.drawable.mascot_afficher_1),
        Mascot("Raymond Le Crayon", R.drawable.mascot2, R.drawable.mascot_afficher_2),
        Mascot("Eustache la Vache", R.drawable.mascot3, R.drawable.mascot_afficher_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascot)
        recyclerView= findViewById(R.id.recycler_mascot)
        recyclerView.setHasFixedSize(true)

        adapter = MascotAdapter(this, listMascot,MascotListener(listMascot, this) , promiseDataBase)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }


}
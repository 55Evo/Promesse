package fr.gof.promesse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.adapter.MascotAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.MascotListener
import fr.gof.promesse.model.Mascot


class ChooseMascotActivity : AppCompatActivity() {
    val promiseDataBase = PromiseDataBase(this)

    lateinit var adapter : MascotAdapter
    lateinit var recyclerView: RecyclerView
    private val listMascot: List<Mascot> = listOf(
        Mascot.JACOU,
        Mascot.RAYMOND,
        Mascot.EUSTACHE
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
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
    private var listMascot: ArrayList<Mascot>? = null
    private val listMascotImages = intArrayOf(
        R.drawable.mascot1,
        R.drawable.mascot2,
        R.drawable.mascot3,
        R.drawable.mascot4
    )
    private val imageNameList =
        arrayOf("Hibou moche", "Oiseau Bg", "Requin chopeur", "mascotte JO ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascot)
        recyclerView= findViewById(R.id.recycler_mascot)
        recyclerView.setHasFixedSize(true)
        listMascot = createListMascot()


        adapter = MascotAdapter(this, listMascot!!,MascotListener(listMascot!!, this ) , promiseDataBase)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun createListMascot(): ArrayList<Mascot> {
        val listMascot: ArrayList<Mascot> = ArrayList()
        for (i in 0 until 4) {
            val mascotModel = Mascot(imageNameList[i], listMascotImages[i])
            listMascot.add(mascotModel)
        }
        return listMascot
    }
}
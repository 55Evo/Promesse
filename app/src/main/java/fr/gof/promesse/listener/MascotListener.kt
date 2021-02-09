package fr.gof.promesse.listener

import android.app.Activity
import android.content.Intent
import fr.gof.promesse.adapter.MascotAdapter
import fr.gof.promesse.MainActivity
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Mascot


class MascotListener(var listMascot: List<Mascot>, var context: Activity): MascotAdapter.OnItemClickListener {
    override fun onItemClick(position: Int, adapter: MascotAdapter, database: PromiseDataBase) {
        var nommascotte : Mascot = listMascot[position]
        database.updateMascot(nommascotte)

        val myIntent = Intent( context, MainActivity::class.java)

        context.startActivity(myIntent)
    }

}
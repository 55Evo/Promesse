package fr.gof.promesse.listener

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import fr.gof.promesse.Adapter.MascotAdapter
import fr.gof.promesse.ChooseMascotActivity
import fr.gof.promesse.MainActivity
import fr.gof.promesse.SearchActivity
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Mascot


class MascotListener(var listMascot: ArrayList<Mascot>, var context: Activity): MascotAdapter.OnItemClickListener {
    override fun onItemClick(position: Int, adapter: MascotAdapter, database: PromiseDataBase) {
        var nommascotte : Mascot = listMascot[position]
        database.updateMascot(nommascotte)

        val toast = Toast.makeText(
            context,
            "Vous avez sélectionné " + utils.user.mascot.name,
            Toast.LENGTH_LONG
        )
        toast.show()

        val myIntent = Intent( context, MainActivity::class.java)

        context.startActivity(myIntent)
    }

}

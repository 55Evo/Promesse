package fr.gof.promesse.listener

import android.app.Activity
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.MainActivity
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import java.util.*


/**
 * Delete button listener
 *
 * @property adapter
 * @property context
 * @property promiseDataBase
 * Listener permettant la suppression de promesse ainsi que la gestion du logo poubelle de suppression
 */
class DeleteButtonListener(var adapter: PromiseAdapter, var context: Activity) :
    View.OnClickListener {
    /**
     * On click
     *
     * @param v
     * permet de faire une rotation sur le logo de poubelle permettant de supprimer une promesse
     * lorsque l'on clique dessus
     */
    override fun onClick(v: View?) {
        v?.clearAnimation()
        v?.animate()?.apply {
            duration = 1000
            rotationXBy(360f)
                .start()
        }

        var listPromesses = adapter.promiseList
        var hasSubtasks = false
        val it2 = listPromesses.iterator()
        while (it2.hasNext()) {
            var p = it2.next()
            if (p.isChecked) {
                if (p.subtasks.size > 0)
                    hasSubtasks = true
            }
        }
        if (hasSubtasks) {
            displayPopup(listPromesses)
        } else {
            deletePromises(listPromesses)
        }
        updateView(v)
    }

    /**
     * Update view
     *
     * @param v
     * Fonction permettant de mettre à jour la visibilité du logo de la poubelle en faisant attention sur
     * quelle activité on se situe
     */
    private fun updateView(v: View?) {
        adapter.inSelection = false
        Handler().postDelayed({ adapter.notifyDataSetChanged() }, 450)
        Handler().postDelayed({
            v?.visibility = View.GONE
            if (context is MainActivity) {
                val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.VISIBLE
            }
        }, 1000)
    }

    /**
     * Display popup
     *
     * @param listPromesses
     * Fonction permettant d'afficher une pop-up lorsque l'on désire supprimer une promesse comportant
     * une ou plusieurs sous-taches
     */
    private fun displayPopup(listPromesses: TreeSet<Promise>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(
            "Attention, au moins une promesse possède des sous-tâches. " +
                    "Êtes-vous sûr(e) de vouloir la supprimer ?"
        )
            .setCancelable(true)
            .setPositiveButton("Oui", DialogInterface.OnClickListener { _, _ ->
                deletePromises(listPromesses)
            })
            .setNegativeButton("Non", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
                for (l in listPromesses) {
                    l.isChecked = false
                    adapter.nbPromisesChecked = 0
                }
            })
        val alert = dialogBuilder.create()

        alert.setTitle("Suppression de promesses")
        alert.show()
    }


    /**
     * Delete promises
     *
     * @param listPromesses
     * @param promiseDataBase
     * Fonction permettant de supprimer une promesse et de remettre l'adapter à jour avec les nouvelles
     * données ainsi que la base de donnée
     */
    private fun deletePromises(listPromesses: TreeSet<Promise>) {
        val it = listPromesses.iterator()
        while (it.hasNext()) {
            var p = it.next()
            if (p.isChecked) {
                val pos = listPromesses.indexOf(p)
                user.deletePromise(p)
                it.remove()
                adapter.nbPromisesChecked = 0
                adapter.notifyItemRemoved(pos)
                adapter.notifyItemRangeChanged(pos, listPromesses.size)
            }
        }
        user.stopDnd(context)


    }
}
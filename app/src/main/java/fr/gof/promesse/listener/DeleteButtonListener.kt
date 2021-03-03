package fr.gof.promesse.listener

import android.app.Activity
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.MainActivity
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise

/**
 * Delete button listener
 *
 * @property adapter
 * @property context
 * @property promiseDataBase
 * @constructor Create empty Delete button listener
 */
class DeleteButtonListener (var adapter : PromiseAdapter, var context : Activity, val promiseDataBase : PromiseDataBase): View.OnClickListener {
    override fun onClick(v: View?) {
        var listPromesses = adapter.promiseList
        var hasSubtasks = false
        val it2 = listPromesses.iterator()
        while(it2.hasNext()) {
            var p = it2.next()
            if(p.isChecked) {
                if(p.subtasks != null)
                    hasSubtasks = true
            }
        }
        if(hasSubtasks) {
            displayPopup(listPromesses)
        }
        else {
            deletePromises(listPromesses, promiseDataBase)
        }
        updateView(v)
    }

    private fun updateView(v: View?) {
        adapter.inSelection = false
        Handler().postDelayed({
            adapter.notifyDataSetChanged()
        }, 400)

        v?.visibility = View.GONE
        if (context is MainActivity) {
            val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
            addButton?.visibility = View.VISIBLE
        }
    }

    private fun displayPopup(listPromesses: MutableList<Promise>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(
            "Attention, au moins une promesse possède des sous-tâches. " +
                    "Êtes-vous sûr(e) de vouloir la supprimer ?"
        )
            .setCancelable(true)
            .setPositiveButton("Oui", DialogInterface.OnClickListener { _, _ ->
                deletePromises(listPromesses, promiseDataBase)
            })
            .setNegativeButton("Non", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
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
     */
    fun deletePromises(listPromesses : MutableList<Promise>, promiseDataBase : PromiseDataBase) {
        val it = listPromesses.iterator()
        while(it.hasNext()) {
            var p = it.next()
            if (p.isChecked) {
                val pos = listPromesses.indexOf(p)
                utils.user.deletePromise(p, promiseDataBase)
                it.remove()
                adapter.notifyItemRemoved(pos)
                adapter.notifyItemRangeChanged(pos, listPromesses.size)
            }
        }

    }
}
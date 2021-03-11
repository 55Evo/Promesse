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
        v?.clearAnimation()
        v?.animate()?.apply {
            duration = 1000
            rotationXBy(360f)
            .start()
        }

        var listPromesses = adapter.promiseList
        var hasSubtasks = false
        val it2 = listPromesses.iterator()
        while(it2.hasNext()) {
            var p = it2.next()
            if(p.isChecked) {
                if(p.subtasks.size > 0 )
                    hasSubtasks = true
            }
        }
        if(hasSubtasks) {
            displayPopup(listPromesses)
        }
        else {
            deletePromises(listPromesses)
        }
        updateView(v)
    }

    private fun updateView(v: View?) {
        adapter.inSelection = false
        adapter.notifyDataSetChanged()
        Handler().postDelayed({
            v?.visibility = View.GONE
            if (context is MainActivity) {
                val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.VISIBLE
            }

        }, 1000)


    }

    private fun displayPopup(listPromesses: MutableList<Promise>) {
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
    private fun deletePromises(listPromesses : MutableList<Promise>) {
        val it = listPromesses.iterator()
        while(it.hasNext()) {
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


    }
}
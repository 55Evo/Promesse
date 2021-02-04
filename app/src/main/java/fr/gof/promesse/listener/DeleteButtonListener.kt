package fr.gof.promesse.listener

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.model.Promise

class DeleteButtonListener (var adapter : PromiseAdapter, var listPromesses : MutableList<Promise>, var context : Activity): View.OnClickListener {
    override fun onClick(v: View?) {
        val it = listPromesses.iterator()
        while (it.hasNext()) {
            var p = it.next()
            if (p.isChecked) {
                //If the item has subtasks, but ask confirmation
                if (p.subtasks != null) {
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage("Attention, cette promesse possède des sous-tâches. " +
                            "Êtes-vous sûr(e) de vouloir la supprimer ?")
                            .setCancelable(true)
                            .setPositiveButton("Oui", DialogInterface.OnClickListener { _, _ ->
                                val pos = listPromesses.indexOf(p)
                                it.remove()
                                adapter.notifyItemRemoved(pos)
                                adapter.notifyItemRangeChanged(pos, listPromesses.size)
                            })
                            .setNegativeButton("Non", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.cancel()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Suppression de promesse")
                    alert.show()
                }
                //If the item has no subtasks, directly delete
                else {
                    val pos = listPromesses.indexOf(p)
                    it.remove()
                    adapter.notifyItemRemoved(pos)
                    adapter.notifyItemRangeChanged(pos, listPromesses.size)
                }
            }

        }
        adapter.inSelection = false
        v?.visibility = View.INVISIBLE
    }

}

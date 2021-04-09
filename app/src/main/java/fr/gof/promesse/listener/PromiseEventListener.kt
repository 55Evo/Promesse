package fr.gof.promesse.listener

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.MainActivity
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.R
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.adapter.SubtaskAdapter
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State

import java.util.*

/**
 * Promise event listener
 *
 * @property listPromesses
 * @property context
 *
 * Lister d'une promesse
 */
class PromiseEventListener(var listPromesses: TreeSet<Promise>, var context: Activity) : PromiseAdapter.OnItemClickListener {
    /**
     * On item click
     *
     * @param position
     * @param adapter
     * Fonction permettant de récupérer l'élément sélectionné et de notifier l'adapter en lui passant
     * le paramètre clic signifiant que l'on a cliqué sur la promesse et donc la déployer
     */
    override fun onItemClick(position: Int, adapter: PromiseAdapter) {
        val clickedItem = listPromesses.elementAt(position)
        clickedItem.isDescDeployed = !clickedItem.isDescDeployed
        var bundle = Bundle()
        bundle.putBoolean("click", true)
        adapter.notifyItemChanged(position, bundle);
    }

    /**
     * Uncheckitems
     *
     * @param adapter
     * Fonction permettant de remettre chaque item de l'adapter à jour en leur retirant leur checkbox
     * et les décochant
     */
    fun uncheckitems(adapter: PromiseAdapter){
        var bundle = Bundle()
        bundle.putBoolean("longclick", true)
        for (i in 0..(listPromesses.size)){
            adapter.notifyItemChanged(i, bundle);
        }
    }

    /**
     * On item long click
     *
     * @param position
     * @param adapter
     *
     * Lister d'un appui long sur une promesse qui met à jour les vues et l'adapter
     */
    override fun onItemLongClick(position: Int, adapter: PromiseAdapter) {
        var clickedItem = listPromesses.elementAt(position)
        if(!adapter.inSelection){
            clickedItem.isChecked = true
            adapter.nbPromisesChecked++
            adapter.inSelection = true
            val deleteButton : FloatingActionButton = context.findViewById(R.id.deleteButton)
            deleteButton.visibility = View.VISIBLE
            if (context is MainActivity) {
                val addButton : FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.GONE
            }
            var bundle = Bundle()
            bundle.putBoolean("longclick", true)
            for (i in 0..(listPromesses.size)){
                adapter.notifyItemChanged(i, bundle);
            }

        } else {
            uncheckItem(clickedItem, adapter)
        }
    }

    /**
     * On item button edit click
     *
     * @param position
     * @param promiseAdapter
     * Listner permettant d'ouvrir l'activité PromiseManagerActivity permettant de modifier une promesse
     */
    override fun onItemButtonEditClick(position: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = promiseAdapter.promiseList.elementAt(position)
        var p = clickedItem.copy()

        val intent = Intent(context, PromiseManagerActivity::class.java)
        intent.putExtra("Promise", p)
        context.startActivity(intent)
    }

    /**
     * On item checked changed
     *
     * @param position
     * @param adapter
     * Fonction appelée lorsque la checkbox d'un item change (sélectionnée ou désélectionnée)
     */
    override fun onItemCheckedChanged(position: Int, adapter: PromiseAdapter) {
        val clickedItem = listPromesses.elementAt(position)
        uncheckItem(clickedItem, adapter)
    }

    /**
     * Uncheck item
     *
     * @param clickedItem
     * @param adapter
     *
     * Fonction qui permet de décocher une promesse et donc de ne pas la supprimer si l'on valide
     * Si j'ai décoché toute les promesse nous arretons le mode suppression en enlevant le bouton
     * supprimer (bouton poubelle en bas de l'écran) et en remettant le bouton d'ajout
     */
    private fun uncheckItem(clickedItem: Promise, adapter: PromiseAdapter) {
        if (clickedItem.isChecked) adapter.nbPromisesChecked--
        else adapter.nbPromisesChecked++
        Log.d("Nbpromessescheck", adapter.nbPromisesChecked.toString())
        clickedItem.isChecked = !clickedItem.isChecked
        if (adapter.nbPromisesChecked == 0) {
            adapter.inSelection = false
            val deleteButton: FloatingActionButton = context.findViewById(R.id.deleteButton)
            deleteButton.visibility = View.GONE
            if (context is MainActivity) {
                val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.VISIBLE
            }
            uncheckitems(adapter)
        }
        else{

        var bundle = Bundle()
        bundle.putBoolean("longclick", true)
            adapter.notifyItemChanged(adapter.promiseList.lastIndexOf(clickedItem), bundle);

        //adapter.notifyItemChanged(adapter.promiseList.lastIndexOf(clickedItem))
        }

    }

    /**
     * On check subtask changed
     *
     * @param position
     * @param promise
     * @param subtaskAdapter
     * @param promiseAdapter
     *
     * Fonction appelée lors du changement d'état de la checkbox d'une soustache elle appellera
     * l'adapter afin qu'il mette uniquement à jour le nouvel état des checkbox et fasse aussi augmenter
     * ou diminuer la barre de réalisatiopn
     */
    override fun onCheckSubtaskChanged(
            position: Int,
            promise: Promise,
            subtaskAdapter: SubtaskAdapter,
            promiseAdapter: PromiseAdapter
    ) {
        var clickedItem = subtaskAdapter.subtaskList[position]
        clickedItem.done = !clickedItem.done
        user.updateDoneSubtask(clickedItem, clickedItem.done)
        //promiseAdapter.notifyItemChanged(position)
        var bundle = Bundle()
        bundle.putBoolean("clicksubtask", true)
        promiseAdapter.notifyItemChanged(promiseAdapter.promiseList.lastIndexOf(promise), bundle)
    }

    /**
     * On item button start click
     *
     * @param posAdapter
     * @param promiseAdapter
     *
     * Fonction appelée lorsque l'on clique sur le bouton start d'une promesse c'est à dire que l'on commence
     * la réalisation d'une promesse/tache. Elle passera l'état de la promesse en IN_PROGRESS
     */
    override fun onItemButtonStartClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses.elementAt(posAdapter)
        clickedItem.state = State.IN_PROGRESS
        user.updatePromise(clickedItem)
        var bundle = Bundle()
        bundle.putBoolean("changestate", true)
        promiseAdapter.notifyItemChanged(posAdapter, bundle)
        if (clickedItem.priority) {
            user.startDnd(context)
        }
    }

    /**
     * On item button stop click
     *
     * @param posAdapter
     * @param promiseAdapter
     * Fonction appelée lorsque l'on arrête la réalisation d'une promesse en cliquant sur le bouton stop
     * elle changera l'état de la promesse en spécifiant à l'adapter de ne notificier uniquement cette vue
     */
    override fun onItemButtonStopClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses.elementAt(posAdapter)
        clickedItem.state = State.TODO
        user.updatePromise(clickedItem)
        var bundle = Bundle()
        bundle.putBoolean("changestate", true)
        promiseAdapter.notifyItemChanged(posAdapter, bundle)
        user.stopDnd(context)
    }

    /**
     * On item button redo click
     *
     * @param posAdapter
     * @param promiseAdapter
     * Fonction appelée lorsque l'utilisateur veut recommencer une promesse déja réalisée en cliquant sur
     * le logo refaire de la promesse. Elle changera l'état de la promesse dans l'adapter ainsi que dans
     * la base de données
     */
    override fun onItemButtonRedoClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses.elementAt(posAdapter)
        clickedItem.state = State.TODO
        user.updatePromise(clickedItem)
        var bundle = Bundle()
        bundle.putBoolean("changestate", true)
        promiseAdapter.notifyItemChanged(posAdapter, bundle)
    }

    /**
     * On item button done click
     *
     * @param posAdapter
     * @param promiseAdapter
     * Fonction appelée lorsque l'on termine une promesse en cliquant sur le bouton terminé de notre promesse
     * On pense à désactiver le mode ne pas déranger si il était activé. On rajoute aussi une notification
     * dans la base de donnée afin de pouvoir informer le destinataire de la réalisation de cette dernière
     *
     */
    override fun onItemButtonDoneClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses.elementAt(posAdapter)
        clickedItem.state = State.DONE
        user.updatePromise(clickedItem)
        var bundle = Bundle()
        bundle.putBoolean("changestate", true)
        promiseAdapter.notifyItemChanged(posAdapter, bundle)
        user.stopDnd(context)
        user.unreadNotification(clickedItem)
    }
}


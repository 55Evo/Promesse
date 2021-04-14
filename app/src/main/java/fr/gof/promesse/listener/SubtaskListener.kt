package fr.gof.promesse.listener

import fr.gof.promesse.adapter.SubtaskEditorAdapter

/**
 * Subtask listener
 *
 */
class SubtaskListener :
    SubtaskEditorAdapter.OnItemClickListener {

    /**
     * On item delete called when an subtask is deleted.
     * It refresh the view.
     *
     * @param position
     * @param adapter
     *
     * Qui est appelée lorsqu'une sous-tâche est supprimée.
     * Permet de rafraîchir la vue.
     */
    override fun onItemDelete(position: Int, adapter: SubtaskEditorAdapter) {
        adapter.subtaskList.removeAt(position)
        adapter.notifyDataSetChanged()
    }

    /**
     * On item checked changed called when a subtask is checked or unchecked.
     * It updates the state of the checkbox of the subtask.
     *
     * @param position
     * @param promiseAdapter
     *
     * Méthode appelée quand l'utilisateur coche ou décoche une sous-tâche.
     * Permet d'actualiser l'état de la checkbox de la sous-tâche.
     */
    override fun onItemCheckedChanged(position: Int, promiseAdapter: SubtaskEditorAdapter) {
        promiseAdapter.subtaskList[position].done = !promiseAdapter.subtaskList[position].done
    }

    /**
     * On item text changed called when user change the text of the subtask
     * and update the subtaskList.
     *
     * @param position
     * @param subtaskEditorAdapter
     * @param text
     *
     * Méthode appelée quand l'utilisateur modifie le texte de la sous-tâche
     * et met à jour la liste des sous-tâches.
     */
    override fun onItemTextChanged(
        position: Int,
        subtaskEditorAdapter: SubtaskEditorAdapter,
        text: String
    ) {
        subtaskEditorAdapter.subtaskList[position].title = text
    }

}

package fr.gof.promesse.listener

import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.adapter.SubtaskEditorAdapter

class SubtaskListener(subtaskList: Any, promiseManagerActivity: PromiseManagerActivity) : SubtaskEditorAdapter.OnItemClickListener {

    override fun onItemDelete(position: Int, subtaskEditorAdapter: SubtaskEditorAdapter) {
        subtaskEditorAdapter.subtaskList.removeAt(position)
        subtaskEditorAdapter.notifyItemRemoved(position)
        subtaskEditorAdapter.notifyItemRangeChanged(position, subtaskEditorAdapter.subtaskList.size)
    }

    override fun onItemCheckedChanged(position: Int, subtaskEditorAdapter: SubtaskEditorAdapter) {
        subtaskEditorAdapter.subtaskList[position].done = !subtaskEditorAdapter.subtaskList[position].done
    }

    override fun onItemTextChanged(position: Int, subtaskEditorAdapter: SubtaskEditorAdapter, text: String) {
        subtaskEditorAdapter.subtaskList[position].title = text
    }

}

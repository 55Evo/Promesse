package fr.gof.promesse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.Subtask

/**
 * Subtask adapter
 *
 * @property promise
 * @property context
 * @property listener
 * @property promiseAdapter
 *
 * adapter des sous taches d'une promesse
 */
class SubtaskAdapter(
    var promise : Promise,
    val context: Context,
    val listener: PromiseAdapter.OnItemClickListener,
    val promiseAdapter: PromiseAdapter
): RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {
    var subtaskList: MutableList<Subtask> = promise.subtasks

    /**
     * Get item count
     *
     */
    override fun getItemCount() = subtaskList.size
    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_subtask,
            parent,
            false)
        return SubtaskViewHolder(itemView)
    }
    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     *
     * Fonction permettant la mise à jour de la vue des sous taches le titre et je la coche si
     * la promesse a été réalisé
     */
    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.subtask = subtaskList[position]
        holder.checkBox.isChecked = holder.subtask.done
        holder.checkBox.text = holder.subtask.title
    }
    /**
     * Promise view holder
     * @param view
     *
     * Permet de récupérer la vue de la checkbox correspondant à la sous tache
     */
    inner class SubtaskViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        lateinit var subtask: Subtask
        var checkBox : CheckBox = view.findViewById(R.id.checkBox)

        init {
            checkBox.setOnClickListener(this)
        }

        /**
         * On click
         *
         * @param v
         * Fonction appelée lors du clic sur une sous tache
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onCheckSubtaskChanged(position,promise, this@SubtaskAdapter, promiseAdapter)
            }
        }
    }

}
package fr.gof.promesse.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Subtask
import java.util.*


/**
 * Promise adapter
 *
 * @property subtaskList
 * @property listener
 *
 * "Adapter" appelé dans la classe PromiseManagerActivity permettant de gérer les sous-tâches
 */
class SubtaskEditorAdapter(
    var subtaskList: MutableList<Subtask>,
    val listener: OnItemClickListener,
    val context: Context
) : RecyclerView.Adapter<SubtaskEditorAdapter.SubtaskViewHolder>() {

    /**
     * Get item count
     *
     */
    override fun getItemCount() = subtaskList.size

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.subtask = subtaskList[position]
        holder.checkBox.isChecked = holder.subtask.done
        holder.substask.setText(holder.subtask.title)
    }

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_subtaskeditor,
            parent,
            false
        )
        return SubtaskViewHolder(itemView)
    }

    /**
     * Subtask view holder
     * @param view
     *
     * On récupère les éléments XML afin de pouvoir interragir avec eux
     */
    inner class SubtaskViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, TextWatcher {
        lateinit var subtask: Subtask
        var substask: EditText = view.findViewById(R.id.editTextSubtask)
        var checkBox: CheckBox = view.findViewById(R.id.checkBoxDone)
        private var buttonDelete: ImageButton = view.findViewById(R.id.buttonDelete)

        init {
            buttonDelete.setOnClickListener(this)
            checkBox.setOnClickListener(this)
            substask.addTextChangedListener(this)
        }

        /**
         * On click
         *
         * @param v
         *
         * Fonction appelée lors du clic sur une sous-tâche. Si on clique sur la checkbox on lui
         * met le bon listener et idem si l'on veut la supprimer
         */
        override fun onClick(v: View?) {
            if (v != null) {
                val position = adapterPosition
                if (v is CheckBox) {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemCheckedChanged(position, this@SubtaskEditorAdapter)
                    }
                } else {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemDelete(position, this@SubtaskEditorAdapter)
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemTextChanged(position, this@SubtaskEditorAdapter, s.toString())
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    /**
     * On item click listener
     *
     */
    interface OnItemClickListener {
        /**
         * On item click
         *
         * @param position
         * @param adapter
         */
        fun onItemDelete(position: Int, adapter: SubtaskEditorAdapter)

        /**
         * On item button edit click
         *
         * @param position
         * @param promiseAdapter
         */
        fun onItemCheckedChanged(position: Int, promiseAdapter: SubtaskEditorAdapter)
        fun onItemTextChanged(
            position: Int,
            subtaskEditorAdapter: SubtaskEditorAdapter,
            text: String
        )
    }

}
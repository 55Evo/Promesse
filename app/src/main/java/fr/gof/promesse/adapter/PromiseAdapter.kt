package fr.gof.promesse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise

/**
 * Promise adapter
 *
 * @property promiseList
 * @property listener
 * @constructor Create empty Promise adapter
 */
class PromiseAdapter  (var promiseList : MutableList<Promise>, val listener : OnItemClickListener): RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>() {

    var inSelection = false
    var nbPromisesChecked = 0

    override fun getItemCount() = promiseList.size

    //Affichage d'un item (appelé quand la liste defile ou quand on notifie un changement)
    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {
        val promise = promiseList[position]
        holder.description.text = promise.description
        holder.date.text = promise.getDateToDoToString()
        holder.titre.text = promise.title
        holder.checkBox.isChecked = promise.isChecked
        holder.checkBox.isVisible = inSelection
        holder.description.maxLines = if (promise.isDescDeployed) 10 else 2
        holder.layoutButtonEdit.visibility = if (promise.isDescDeployed) View.VISIBLE else View.GONE
        holder.layout.setBackgroundResource( if (promise.priority) R.drawable.layout_border_important else R.drawable.layout_border)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.promise_item, parent, false)
        return PromiseViewHolder(itemView)
    }


    /**
     * Promise view holder
     *
     * @constructor
     *
     * @param view
     */// HOLDER
    inner class PromiseViewHolder (view : View): RecyclerView.ViewHolder(view),
            View.OnClickListener,
            View.OnLongClickListener {
        var titre : TextView = view.findViewById(R.id.title)
        var date : TextView = view.findViewById(R.id.date)
        var description : TextView = view.findViewById(R.id.description)
        var checkBox : CheckBox = view.findViewById(R.id.delCheckBox)
        var layout : LinearLayout = view.findViewById(R.id.linearlayoutitem)
        var layoutButtonEdit : LinearLayout = view.findViewById(R.id.layoutButtonEdit)
        var buttonEdit : Button = view.findViewById(R.id.buttonEdit)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            buttonEdit.setOnClickListener(this)
            checkBox.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v is CheckBox){
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemCheckedChanged(position, this@PromiseAdapter)
                }
            } else {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    if (v is Button) {
                        listener.onItemButtonEditClick(position, this@PromiseAdapter)
                    } else {
                        listener.onItemClick(position, this@PromiseAdapter)
                    }
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position, this@PromiseAdapter)
            }
            return true
        }
    }

    /**
     * On item click listener
     *
     * @constructor Create empty On item click listener
     *///Interface des events de la liste
    interface OnItemClickListener {
        /**
         * On item click
         *
         * @param position
         * @param adapter
         */
        fun onItemClick(position: Int, adapter : PromiseAdapter)

        /**
         * On item long click
         *
         * @param position
         * @param adapter
         */
        fun onItemLongClick(position: Int, adapter : PromiseAdapter)

        /**
         * On item button edit click
         *
         * @param position
         * @param promiseAdapter
         */
        fun onItemButtonEditClick(position: Int, promiseAdapter: PromiseAdapter)
        fun onItemCheckedChanged(position: Int, promiseAdapter: PromiseAdapter)
    }



}
package fr.gof.promesse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise

class PromiseAdapter  (public var promiseList : MutableList<Promise>, val listener : OnItemClickListener): RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>() {

    public var inSelection = false

    override fun getItemCount() = promiseList.size


    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {
        val promise = promiseList[position]
        holder.description.text = promise.description
        holder.date.text = promise.dateTodo.toString()
        holder.titre.text = promise.title
        holder.checkBox.isChecked = promise.isChecked
        holder.checkBox.isVisible = inSelection
        holder.description.maxLines = if (promise.isDescDeployed) 10 else 2
        holder.layout.setBackgroundResource( if (promise.priority) R.drawable.layout_border_important else R.drawable.layout_border)
        //holder.layout.setPadding(20, 20, 20, 20)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layoutsearchitems, parent, false)
        return PromiseViewHolder(itemView)
    }


    // HOLDER
    inner class PromiseViewHolder (view : View): RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        var titre : TextView = view.findViewById(R.id.title)
        var date : TextView = view.findViewById(R.id.date)
        var description : TextView = view.findViewById(R.id.description)
        var checkBox : CheckBox = view.findViewById(R.id.delCheckBox)
        var layout : LinearLayout = view.findViewById(R.id.linearlayoutitem)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            checkBox.setOnCheckedChangeListener{_, isChecked ->
                if (view.parent != null) {
                    promiseList[(view.parent as RecyclerView).getChildAdapterPosition(view)].isChecked = isChecked
                }

            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, this@PromiseAdapter)
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

    interface OnItemClickListener {
        fun onItemClick(position: Int, adapter : PromiseAdapter)
        fun onItemLongClick(position: Int, adapter : PromiseAdapter)
    }



}
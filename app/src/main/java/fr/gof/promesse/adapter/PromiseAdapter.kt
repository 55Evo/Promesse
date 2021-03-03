package fr.gof.promesse.adapter

import android.content.Context
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import java.util.*


/**
 * Promise adapter
 *
 * @property promiseList
 * @property listener
 * @constructor Create empty Promise adapter
 */
class PromiseAdapter(var promiseList: MutableList<Promise>, val listener: OnItemClickListener, val context : Context): RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>(), IItemTouchHelperAdapter {

    var inSelection = false
    var nbPromisesChecked = 0
    var lastPosition =  -1

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
        holder.layout.setBackgroundResource(if (promise.priority) {
            if (promise.state == State.DONE) R.drawable.layout_border_important_done else R.drawable.layout_border_important
        } else {
            if (promise.state == State.DONE) R.drawable.layout_border_done else R.drawable.layout_border
        })


        if (holder.adapterPosition > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            (holder as PromiseViewHolder).startAnimation(animation)
            lastPosition = holder.adapterPosition
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.promise_item, parent, false)
        return PromiseViewHolder(itemView)
    }

    fun restoreItem(promise: Promise, position: Int, dataBase: PromiseDataBase) {
        promiseList.add(position, promise)
        dataBase.updateDate(promise)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(promiseList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
    override fun onItemDismiss(position: Int) {
        promiseList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Promise view holder
     *
     * @constructor
     *
     * @param view
     */// HOLDER
    inner class PromiseViewHolder(view: View): RecyclerView.ViewHolder(view),
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

        fun startAnimation(animation: Animation) {
            super.itemView.startAnimation(animation)

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
        fun onItemClick(position: Int, adapter: PromiseAdapter)

        /**
         * On item long click
         *
         * @param position
         * @param adapter
         */
        fun onItemLongClick(position: Int, adapter: PromiseAdapter)

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
interface IItemTouchHelperAdapter {
    /**
     * Called when item is moved
     *
     * @param fromPosition The starting point of the item being operated
     * @param toPosition The end point of the item being operated
     */
    fun onItemMove(fromPosition: Int, toPosition: Int)

    /**
     * Called when item is skid
     *
     * @param position The position of the item being skided
     */
    fun onItemDismiss(position: Int)
}
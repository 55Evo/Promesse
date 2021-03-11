package fr.gof.promesse.adapter

import android.content.Context
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
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
class PromiseAdapter(
    var promiseList: MutableList<Promise>,
    val listener: OnItemClickListener,
    val context: Context,
    var displayDate: Boolean = true
): RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>(), IItemTouchHelperAdapter {

    var inSelection = false
    var nbPromisesChecked = 0
    private var lastPosition =  -1
    private var sortedCategory = false


    override fun getItemCount() = promiseList.size

    //Affichage d'un item (appelé quand la liste defile ou quand on notifie un changement)
    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {
        holder.promise = promiseList[position]

        holder.date.text = holder.promise.getDateToString()
        holder.titre.text = holder.promise.title
        holder.description.text = holder.promise.description
        holder.logo.setImageResource(holder.promise.category.image_drawable)
        holder.imageViewCategoryGlobal.setImageResource(holder.promise.category.image_drawable)

        holder.checkBox.isChecked = holder.promise.isChecked
        holder.checkBox.isVisible = inSelection
        holder.description.maxLines = if (holder.promise.isDescDeployed) 10 else 2
        holder.description.minLines = 3
        val deployed = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
        holder.layoutButtonEdit.visibility = deployed
        holder.progressBar.max = holder.promise.subtasks.size
        holder.progressBar.setProgress(holder.promise.getNbStDone(), true)
        holder.rvSubtasks.adapter = SubtaskAdapter(holder.promise.subtasks, context, listener, this)
        //btaskAdapter(holder.promise.subtasks, context, listener)
        holder.rvSubtasks.setHasFixedSize(true)
        holder.rvSubtasks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.progressBar.visibility = deployed

        holder.rvSubtasks.visibility = deployed
        holder.layout.setBackgroundResource(if (holder.promise.priority) {
            if (holder.promise.state == State.DONE) R.drawable.layout_border_important_done else R.drawable.layout_border_important
        } else {
            if (holder.promise.state == State.DONE) R.drawable.layout_border_done else R.drawable.layout_border
        })
        holder.imageViewCategoryGlobal.setBackgroundResource(R.drawable.layout_bubble1)


        if (lastPosition<4)
            lastPosition = holder.adapterPosition
        else if (holder.adapterPosition > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            (holder as PromiseViewHolder).startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
        if (!displayDate)
        holder.date.visibility = View.GONE
        holder.description.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE

        if (sortedCategory) {
            holder.logo.visibility = View.GONE

            if((position != 0) and (position!=-1) ){
                if (holder.promise.category.nom == promiseList[position - 1].category.nom)
                    holder.imageViewCategoryGlobal.visibility = View.GONE
                else{
                    setMargins(holder.layout, 0, 0, 0, 0)
                }
            }
        }
        else{
            holder.imageViewCategoryGlobal.visibility = View.GONE
        }
        if(!holder.promise.isDescDeployed) {
            val zoomout = AnimationUtils.loadAnimation(context, R.anim.zoomout)

            holder.logo.animation = zoomout
        }
        else if (holder.promise.isDescDeployed){
            val zoomin = AnimationUtils.loadAnimation(context, R.anim.zoomin)
            holder.logo.animation = zoomin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.promise_item,
            parent,
            false)
        return PromiseViewHolder(itemView)
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
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
        lateinit var promise:Promise

        var titre : TextView = view.findViewById(R.id.title)
        var logo : ImageView = view.findViewById(R.id.logo)
        var imageViewCategoryGlobal : ImageView = view.findViewById(R.id.imageViewCategoryGlobal)

        var date : TextView = view.findViewById(R.id.date)
        var description : TextView = view.findViewById(R.id.description)
        var checkBox : CheckBox = view.findViewById(R.id.delCheckBox)
        var layout : LinearLayout = view.findViewById(R.id.linearlayoutitem)
        var layoutButtonEdit : ConstraintLayout = view.findViewById(R.id.layoutButtonEdit)
        var buttonEdit : Button = view.findViewById(R.id.buttonEdit)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        var rvSubtasks : RecyclerView = view.findViewById(R.id.recyclerViewSubtask)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            buttonEdit.setOnClickListener(this)
            checkBox.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v!=null) {
                val position = adapterPosition
                if(v is CheckBox) {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemCheckedChanged(position, this@PromiseAdapter)
                        this@PromiseAdapter.notifyDataSetChanged()
                    }
                } else {
                    if (position != RecyclerView.NO_POSITION) {
                        if (v is Button) {
                            listener.onItemButtonEditClick(position, this@PromiseAdapter)
                        } else {
                          listener.onItemClick(position, this@PromiseAdapter)
                            Log.d("____________________<--------->_______________",
                                promise.isDescDeployed.toString())
                            description.visibility = if (!promise.isDescDeployed) View.VISIBLE else View.GONE


                            //promise.isDescDeployed = !promise.isDescDeployed

                        }
                    }
                }
                this@PromiseAdapter.notifyItemChanged(position)
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
        fun onCheckSubtaskChanged(position: Int, subtaskAdapter: SubtaskAdapter, promiseAdapter: PromiseAdapter)
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
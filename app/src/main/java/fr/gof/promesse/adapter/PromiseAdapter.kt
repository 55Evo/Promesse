package fr.gof.promesse.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Property
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.MainActivity
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.SlideAnimation
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import java.util.*
import fr.gof.promesse.MainActivity.Companion.user


/**
 * Promise adapter
 *
 * @property promiseList
 * @property listener
 * @constructor Create empty Promise adapter
 */
class PromiseAdapter(
    val promiseList: TreeSet<Promise>,
    val listener: OnItemClickListener,
    val context: Context,
    var displayDate: Boolean = true
) : RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>(), IItemTouchHelperAdapter {

    var inSelection = false
    var nbPromisesChecked = 0
    private var lastPosition = -1
    private var sortedCategory = false
    var displayAnimation = false


    override fun getItemCount() = promiseList.size

    //Affichage d'un item (appelé quand la liste defile ou quand on notifie un changement)
    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {

        holder.promise = promiseList.elementAt(position)
        if (holder.promise.isDescDeployed) holder.logo.layoutParams.width = 250 else holder.logo.layoutParams.width = 150
        if (holder.promise.isDescDeployed) holder.logo.layoutParams.height = 250 else holder.logo.layoutParams.height = 150
        holder.date.text = holder.promise.getDateToString()
        holder.titre.text = holder.promise.title
        if (holder.promise.isDescDeployed) holder.titre.textSize = 25f else holder.titre.textSize =18f
        holder.description.text = holder.promise.description
        holder.logo.setImageResource(holder.promise.category.image_drawable)
        holder.imageViewCategoryGlobal.setImageResource(holder.promise.category.image_drawable)
        holder.checkBox.isChecked = holder.promise.isChecked
        holder.checkBox.isVisible = inSelection
//        holder.description.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
//        holder.description.maxLines = if (holder.promise.isDescDeployed) 10 else 2
       // holder.description.minLines = 3
        val deployed = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
        holder.deployed.visibility = deployed
        //holder.layoutButtonEdit.visibility = deployed
        holder.progressBar.max = holder.promise.subtasks.size
        holder.progressBar.setProgress(holder.promise.getNbStDone(), true)

        statePromiseUpdate(holder)

        holder.rvSubtasks.adapter = SubtaskAdapter(holder.promise, context, listener, this)
        holder.rvSubtasks.setHasFixedSize(true)
        holder.rvSubtasks.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
       // holder.rvSubtasks.visibility = deployed
        holder.imageViewCategoryGlobal.setBackgroundResource(R.drawable.layout_bubble1)
        if (lastPosition < 3)
            lastPosition = holder.adapterPosition
        else if (holder.adapterPosition > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.deployed_item)
            holder.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
        if (!displayDate)
            holder.date.visibility = View.GONE
        //holder.description.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE

        if (sortedCategory) {
            holder.logo.visibility = View.GONE

            if ((position != 0) and (position != -1)) {
                if (holder.promise.category.nom == promiseList.elementAt(position - 1).category.nom)
                    holder.imageViewCategoryGlobal.visibility = View.GONE
                else {
                    setMargins(holder.layout, 0, 0, 0, 0)
                }
            }
        } else {
            holder.imageViewCategoryGlobal.visibility = View.GONE
        }
//        var choice = if (holder.promise.isDescDeployed) R.anim.zoomin else R.anim.zoomout
//        if (holder.promise.isDescDeployed) {
//            holder.logo.animation = AnimationUtils.loadAnimation(context, choice)
//            holder.logo.startAnimation(holder.logo.animation)
//        } else {
//            holder.logo.animation = AnimationUtils.loadAnimation(context, choice)
//            holder.logo.startAnimation(holder.logo.animation)
//        }

    }

    private fun statePromiseUpdate(holder: PromiseViewHolder) {
        when (holder.promise.state) {
            State.IN_PROGRESS -> {
                holder.buttonStart.visibility = View.GONE
                holder.buttonStop.visibility = View.VISIBLE
                holder.buttonDone.visibility = View.VISIBLE
                holder.buttonRedo.visibility = View.GONE
                holder.layout.setBackgroundResource(
                        if (holder.promise.priority) {
                            R.drawable.layout_border_important_inprogress
                        } else {
                            R.drawable.layout_border_inprogress
                        }
                )
                holder.notifDisabled.visibility = if(holder.promise.priority) if (inSelection) View.GONE else View.VISIBLE else View.GONE
                holder.textViewInprogress.visibility = View.VISIBLE
            }
            State.DONE -> {
                holder.buttonStart.visibility = View.GONE
                holder.buttonStop.visibility = View.GONE
                holder.buttonDone.visibility = View.GONE
                holder.buttonRedo.visibility = View.VISIBLE
                holder.layout.setBackgroundResource(
                        if (holder.promise.priority) {
                            R.drawable.layout_border_important_done
                        } else {
                            R.drawable.layout_border_done
                        }
                )
                holder.textViewInprogress.visibility = View.GONE
                holder.notifDisabled.visibility = View.GONE
            }
            State.TODO -> {
                holder.buttonStart.visibility = if (inSelection) View.GONE else View.VISIBLE
                holder.buttonStop.visibility = View.GONE
                holder.buttonDone.visibility = View.GONE
                holder.buttonRedo.visibility = View.GONE
                holder.layout.setBackgroundResource(
                        if (holder.promise.priority) {
                            R.drawable.layout_border_important
                        } else {
                            R.drawable.layout_border
                        }
                )
                holder.textViewInprogress.visibility = View.GONE
                holder.notifDisabled.visibility = View.GONE
            }
        }
    }

    override fun onBindViewHolder(
        holder: PromiseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        Log.d("_______________________________yyyyyyyyyyyyyyyyyyyyyyyyy__________________________________oooo",
            "la")
        if (payloads != null && payloads.isNotEmpty()) {
            var bundle = payloads[0] as Bundle
            var click: Boolean? = bundle.getBoolean("click")
            var long: Boolean? = bundle.getBoolean("longclick")
            var subtask: Boolean? = bundle.getBoolean("clicksubtask")
            var changeState: Boolean? = bundle.getBoolean("changestate")
            var lastPayload =
                payloads[payloads.size - 1]
            if (click as Boolean){
                println("testeeee")
                if (click){
                    println("test<0")
                    refreshPromise(holder, click)
                }}
            if (long as Boolean){
                println("testeeee")
                if (long){
                    refreshDelete(holder, long)
                }
            }
            if (subtask as Boolean){
                println("")
                if (subtask){
                    refreshSubtask(holder, subtask)
                }
            }
            if (changeState as Boolean){
                println("")
                if (changeState){
                    refreshState(holder)
                }
            }

        } else
            onBindViewHolder(holder, position)
    }

    fun refreshSubtask(holder: PromiseViewHolder, fold: Boolean){
        holder.progressBar.max = holder.promise.subtasks.size
        holder.progressBar.setProgress(holder.promise.getNbStDone(), true)
        holder.rvSubtasks.adapter = SubtaskAdapter(holder.promise, context, listener, this)
        holder.rvSubtasks.setHasFixedSize(true)
        holder.rvSubtasks.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }


    fun showOffDdelete(){
        val deleteButton : FloatingActionButton = (context as Activity).findViewById(R.id.deleteButton)
        deleteButton.visibility = View.GONE
        if (context is MainActivity) {
            val addButton : FloatingActionButton = context.findViewById(R.id.buttonAdd)
            addButton.visibility = View.VISIBLE
        }
    }
    fun refreshDelete(holder: PromiseViewHolder, fold: Boolean) {
        holder.promise = promiseList.elementAt(holder.adapterPosition)
        holder.checkBox.isChecked = holder.promise.isChecked
        holder.checkBox.isVisible = inSelection
        refreshState(holder)
        //holder.promise = promiseList[position]
    }
    fun refreshState(holder: PromiseViewHolder) {
        holder.promise = promiseList.elementAt(holder.adapterPosition)
        statePromiseUpdate(holder)
    }
    private fun refreshPromise(holder: PromiseViewHolder, fold: Boolean) {

        holder.promise = promiseList.elementAt(holder.adapterPosition)
        var isdepl = holder.promise.isDescDeployed
//        var choice = if (isdepl) R.anim.zoomin else R.anim.zoomout
        val currentWidth: Int = 100
        val currentWidthEnd =250
        val currentWidthStart = 150
        if (holder.promise.isDescDeployed) holder.titre.textSize = 22f else holder.titre.textSize = 18f

        if (holder.promise.isDescDeployed) {
                        val animator1: ObjectAnimator =
                ObjectAnimator.ofInt(holder.logo, WidthProperty(), currentWidthStart, currentWidthEnd)
            animator1.duration = 300
            animator1.interpolator = DecelerateInterpolator()
            animator1.start()
            } else {
            val animator1: ObjectAnimator =
                ObjectAnimator.ofInt(holder.logo, WidthProperty(), currentWidthEnd, currentWidthStart)
            animator1.duration = 300
            animator1.interpolator = DecelerateInterpolator()
            animator1.start()
            }
        if (!isdepl){

            var animation = SlideAnimation(holder.deployed)
            var animation1 = SlideAnimation(holder.description)
//            animation1.expand(holder.promise.isDescDeployed)
            animation.expand(holder.promise.isDescDeployed)
            val tv = holder.titre
            val endSize = 18f
            val startSize = 25f
            val animationDuration = 300 // Animation duration in ms
            val animator: ValueAnimator = ObjectAnimator.ofFloat(tv, "textSize", startSize, endSize)
            animator.duration = animationDuration.toLong()
            animator.start()
            Log.d("-------------------------------------------------------------- :::::::: ",currentWidth.toString())
            holder.titre.animate()
            Handler().postDelayed({
                holder.deployed.visibility =
                    if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE

            }, 300)
        }
        else{
            //holder.description.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
            //holder.description.maxLines = if (holder.promise.isDescDeployed) 10 else 2
            var animation1 = SlideAnimation(holder.description)
//            animation1.expand(holder.promise.isDescDeployed)
            var animation = SlideAnimation(holder.deployed)
            animation.expand(holder.promise.isDescDeployed)
            holder.titre.animate()
            holder.deployed.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
            holder.layoutButtonEdit.animation = AnimationUtils.loadAnimation(context,
                R.anim.deployed_item)
            holder.progressBar.animation = AnimationUtils.loadAnimation(context,
                R.anim.deployed_item)

            holder.rvSubtasks.animation = AnimationUtils.loadAnimation(context,
                R.anim.deployed_item)
        holder.layoutButtonEdit.animate()
        holder.progressBar.animate()
        holder.rvSubtasks.animate()

            val tv = holder.titre

            val endSize = 25f
            val startSize = 18f
            val animationDuration = 300 // Animation duration in ms


            val animator: ValueAnimator = ObjectAnimator.ofFloat(tv, "textSize", startSize, endSize)
            animator.duration = animationDuration.toLong()

            animator.start()

        }
//        if (holder.promise.isDescDeployed) holder.titre.textSize = 22f else holder.titre.textSize = 18f
        //holder.description.maxLines = if (holder.promise.isDescDeployed) 10 else 2
        //holder.description.minLines = 3

//        Handler().postDelayed({
//            holder.deployed.visibility = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
////            holder.description.visibility =
////                if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
////            val deployed = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
////            holder.layoutButtonEdit.visibility = deployed
//            holder.layoutButtonEdit.animation = AnimationUtils.loadAnimation(context, R.anim.deployed_item)
//            holder.progressBar.animation = AnimationUtils.loadAnimation(context, R.anim.deployed_item)
//
//            holder.rvSubtasks.animation = AnimationUtils.loadAnimation(context, R.anim.deployed_item)
////            holder.progressBar.visibility = deployed
////            holder.rvSubtasks.visibility = deployed
//        }, 700)

//        holder.layoutButtonEdit.animate()
//        holder.progressBar.animate()
//        holder.rvSubtasks.animate()

//        var animation = SlideAnimation(holder.deployed)
//        animation.expand(holder.promise.isDescDeployed)

// this interpolator only speeds up as it keeps going

// this interpolator only speeds up as it keeps going
//        animation.interpolator = AccelerateInterpolator()
        //animation.duration = 3000
//        messageView.setAnimation(animation)
//        messageView.startAnimation(animation)


       // holder.layout.animation = animation
        //holder.layout.startAnimation(animation)
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
        promiseList.add(promise)
        dataBase.updateDate(promise)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        //Collections.swap(promiseList.toMutableList(), fromPosition, toPosition)
        var save = promiseList.elementAt(fromPosition).dateTodo
        promiseList.elementAt(fromPosition).dateTodo = promiseList.elementAt(toPosition).dateTodo
        promiseList.elementAt(toPosition).dateTodo = save
        var fromPos = promiseList.elementAt(fromPosition)
        var toPos = promiseList.elementAt(toPosition)
        promiseList.remove(fromPos)
        promiseList.remove(toPos)
        promiseList.add(fromPos)
        promiseList.add(toPos)
        user.updatePromise(fromPos)
        user.updatePromise(toPos)
        notifyItemMoved(fromPosition, toPosition)


    }

    override fun onItemDismiss(position: Int) {
        promiseList.remove(promiseList.elementAt(position))
        notifyItemRemoved(position)
    }

    /**
     * Promise view holder
     *
     * @constructor
     *
     * @param view
     */// HOLDER
    inner class PromiseViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener,
        View.OnLongClickListener {
        lateinit var promise: Promise
        lateinit var savePromise: Promise
        var titre: TextView = view.findViewById(R.id.title)
        var logo: ImageView = view.findViewById(R.id.logo)
        var imageViewCategoryGlobal: ImageView = view.findViewById(R.id.imageViewCategoryGlobal)
        var date: TextView = view.findViewById(R.id.date)
        var description: TextView = view.findViewById(R.id.description)
        var checkBox: CheckBox = view.findViewById(R.id.delCheckBox)
        var layout: LinearLayout = view.findViewById(R.id.linearlayoutitem)
        var layoutButtonEdit: ConstraintLayout = view.findViewById(R.id.layoutButtonEdit)
        var buttonEdit: Button = view.findViewById(R.id.buttonEdit)
        var buttonStart: Button = view.findViewById(R.id.buttonStart)
        var buttonRedo: Button = view.findViewById(R.id.buttonRedo)
        var buttonStop: Button = view.findViewById(R.id.buttonStop)
        var buttonDone: Button = view.findViewById(R.id.buttonDone)
        var textViewInprogress: TextView = view.findViewById(R.id.textViewInProgress)
        var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        var rvSubtasks: RecyclerView = view.findViewById(R.id.recyclerViewSubtask)
        var deployed : LinearLayout = view.findViewById(R.id.deployedLayout)
        var notifDisabled : ImageView = view.findViewById(R.id.notifDisabled)
        var posAdapter: Int = 0
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            buttonEdit.setOnClickListener(this)
            buttonStart.setOnClickListener(this)
            buttonDone.setOnClickListener(this)
            buttonRedo.setOnClickListener(this)
            buttonStop.setOnClickListener(this)
            checkBox.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                posAdapter = adapterPosition
                if (v is CheckBox) {
                    if (posAdapter != RecyclerView.NO_POSITION) {
                        listener.onItemCheckedChanged(posAdapter, this@PromiseAdapter)
                    }

                } else {
                    if (posAdapter != RecyclerView.NO_POSITION) {
                        if (v is Button) {
                            when (v.id) {
                                R.id.buttonEdit -> listener.onItemButtonEditClick(posAdapter, this@PromiseAdapter)
                                R.id.buttonStart -> listener.onItemButtonStartClick(posAdapter, this@PromiseAdapter)
                                R.id.buttonStop -> listener.onItemButtonStopClick(posAdapter, this@PromiseAdapter)
                                R.id.buttonRedo -> listener.onItemButtonRedoClick(posAdapter, this@PromiseAdapter)
                                R.id.buttonDone -> listener.onItemButtonDoneClick(posAdapter, this@PromiseAdapter)
                            }
                        } else {
                            displayAnimation = true
                            listener.onItemClick(posAdapter, this@PromiseAdapter)
                        }
                    }
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            posAdapter = adapterPosition
            if (posAdapter != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(posAdapter, this@PromiseAdapter)
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
        fun onCheckSubtaskChanged(
            position: Int,
            promise: Promise,
            subtaskAdapter: SubtaskAdapter,
            promiseAdapter: PromiseAdapter
        )

        fun onItemButtonStartClick(posAdapter: Int, promiseAdapter: PromiseAdapter)
        fun onItemButtonStopClick(posAdapter: Int, promiseAdapter: PromiseAdapter)
        fun onItemButtonRedoClick(posAdapter: Int, promiseAdapter: PromiseAdapter)
        fun onItemButtonDoneClick(posAdapter: Int, promiseAdapter: PromiseAdapter)
    }
    internal class WidthProperty : Property<View, Int>(Int::class.java, "width") {

        override fun get(view: View): Int {
            return view.width
        }

        override fun set(view: View, value: Int?) {
            view.layoutParams.width = value!!
            view.layoutParams.height= value!!
            view.layoutParams = view.layoutParams
        }
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
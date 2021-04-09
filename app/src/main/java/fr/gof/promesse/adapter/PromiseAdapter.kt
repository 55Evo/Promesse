package fr.gof.promesse.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
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
 *
 * On créé un adapter pour les promesses celui ci permet d'afficher toutes les vues de notre
 * recycler view contenant les promesses
 *
 *
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
    // expérimental c'était dans le cas ou on voulait afficher les promesses de façon triées
    // par catégorie mais cela n'est pas terminé ou concluant
    private var sortedCategory = false

    /**
     * Get item count
     * retourne la taille de la liste des promesses de l'adapter
     */
    override fun getItemCount() = promiseList.size

    //Affichage d'un item (appelé quand la liste defile ou quand on notifie un changement)
    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     *
     * cette fonction est appelé lors du refresh de l'adapter global
     */
    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {
        //je récupère la promesse
        holder.promise = promiseList.elementAt(position)
        setHoldersElements(holder)
        setAnimations(holder)
        setViews(holder, position)


    }

    /**
     * Set holders elements
     *
     * @param holder
     *
     * Fonction qui met à jour la vue d'une promesse
     */
    private fun setHoldersElements(holder: PromiseViewHolder) {
        if (holder.promise.isDescDeployed) holder.logo.layoutParams.width =
            utils.currentWidthEnd else holder.logo.layoutParams.width = utils.currentWidthStart
        if (holder.promise.isDescDeployed) holder.logo.layoutParams.height =
            utils.currentWidthEnd else holder.logo.layoutParams.height = utils.currentWidthStart
        holder.date.text = holder.promise.getDateToString()
        holder.titre.text = holder.promise.title
        if (holder.promise.recipient.isNotEmpty()) {
            holder.recipient.visibility = View.VISIBLE
            holder.recipient.text = context.getString(R.string.recipient) + ": "+holder.promise.recipient
        } else {
            holder.recipient.visibility = View.GONE
        }

        if (holder.promise.isDescDeployed) holder.titre.textSize = utils.endSize else holder.titre.textSize =
            utils.startSize
        holder.description.text = holder.promise.description
        holder.logo.setImageResource(holder.promise.category.image_drawable)
        holder.imageViewCategoryGlobal.setImageResource(holder.promise.category.image_drawable)
        holder.checkBox.isChecked = holder.promise.isChecked
        holder.checkBox.isVisible = inSelection
        holder.progressBar.max = holder.promise.subtasks.size
        holder.progressBar.setProgress(holder.promise.getNbStDone(), true)
        statePromiseUpdate(holder)
        holder.rvSubtasks.adapter = SubtaskAdapter(holder.promise, context, listener, this)
        holder.rvSubtasks.setHasFixedSize(true)
        holder.rvSubtasks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.imageViewCategoryGlobal.setBackgroundResource(R.drawable.layout_bubble1)
    }

    /**
     * Set animations
     *
     * @param holder
     *
     * Fonction qui permet d'animer le défilement des promesses lorsque celles ci ne sont pas déja chargées
     * Cependant seulement les 3 premières déja chargées afin de réduire un peu les animations
     */
    private fun setAnimations(holder: PromiseViewHolder) {
        if (lastPosition < 3)
            lastPosition = holder.adapterPosition
        else if (holder.adapterPosition > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.deployed_item)
            holder.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
    }

    /**
     * Set views
     *
     * @param holder
     * @param position
     * On affiche la date et mise à jouors des vues
     */
    private fun setViews(
        holder: PromiseViewHolder,
        position: Int
    ) {
        val deployed = if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
        holder.deployed.visibility = deployed

        if (!displayDate)
            holder.date.visibility = View.GONE

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
    }

    /**
     * State promise update
     *
     * @param holder
     * Met à jour le statut d'une promesse il peut etre en
     * IN_PROGRESS quand la promesse est en cours de réalisation
     * DONE quand la promesse a été réalisé
     * TO DO quand la promesse n'a pas encore été réalisé
     */
    private fun statePromiseUpdate(holder: PromiseViewHolder) {
        when (holder.promise.state) {
            State.IN_PROGRESS -> {
                updateInProgress(holder)
            }
            State.DONE -> {
                updateDone(holder)
            }
            State.TODO -> {
                updateTodo(holder)
            }
        }
    }

    /**
     * UpdateTodo
     *
     * @param holder
     *
     * on met  à jour la promesse quand elle est dans l'état TO DO
     */
    private fun updateTodo(holder: PromiseViewHolder) {
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
        holder.textViewInProgress.visibility = View.GONE
        holder.notifDisabled.visibility = View.GONE
    }

    /**
     * Update done
     *
     * @param holder
     * on met  à jour la promesse quand elle est dans l'état DONE
     */
    private fun updateDone(holder: PromiseViewHolder) {
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
        holder.textViewInProgress.visibility = View.GONE
        holder.notifDisabled.visibility = View.GONE
    }

    /**
     * Update in progress
     *
     * @param holder
     * on met  à jour la promesse quand elle est dans l'état IN_PROGRESS
     */
    private fun updateInProgress(holder: PromiseViewHolder) {
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
        holder.notifDisabled.visibility =
            if (holder.promise.priority) if (inSelection) View.GONE else View.VISIBLE else View.GONE
        holder.textViewInProgress.visibility = View.VISIBLE
    }

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     * @param payloads
     *
     * Override de la fonction permettant de mettre à jour les vues
     * On lui passe une payload ce qui lui permet de mettre à jour uniquement les éléments de
     * la vue concernés par cette mise à jour
     * On récupère des booléens en fonction de la clé passéé, on met à jour différemment
     * en fonction de si l'on a effectué un clic sur une sous tache par exemple que lorsque l'on
     * clique sur une promesse pour la déployer
     */
    override fun onBindViewHolder(
        holder: PromiseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads != null && payloads.isNotEmpty()) {
            var bundle = payloads[0] as Bundle
            var click: Boolean? = bundle.getBoolean("click")
            var long: Boolean? = bundle.getBoolean("longclick")
            var subtask: Boolean? = bundle.getBoolean("clicksubtask")
            var changeState: Boolean? = bundle.getBoolean("changestate")
            var lastPayload =
                payloads[payloads.size - 1]
            if (click as Boolean){
                if (click){ refreshPromise(holder)
                }}
           else if (long as Boolean){
                if (long){ refreshDelete(holder)
                }
            }
            else if (subtask as Boolean){
                if (subtask){ refreshSubtask(holder)
                }
            }
            else if (changeState as Boolean){
                if (changeState){ refreshState(holder)
                }
            }
        } else
            onBindViewHolder(holder, position)
    }

    /**
     * Refresh subtask
     *
     * @param holder
     * @param fold
     * Dans le cas du refresh de la sous tache on met uniquement la vue concernant la progress bar
     * à jour afin de n'impacter le visuel de l'interface uniquement sur la vue de la progress bar
     */
    fun refreshSubtask(holder: PromiseViewHolder){
        holder.progressBar.max = holder.promise.subtasks.size
        holder.progressBar.setProgress(holder.promise.getNbStDone(), true)
        holder.rvSubtasks.adapter = SubtaskAdapter(holder.promise, context, listener, this)
        holder.rvSubtasks.setHasFixedSize(true)
        holder.rvSubtasks.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    /**
     * Show off ddelete
     *
     * On met à jour le bouton délete et on le remplace par le bouton d'ajout d'une promesse
     */
    fun showOffDdelete(){
        val deleteButton : FloatingActionButton = (context as Activity).findViewById(R.id.deleteButton)
        deleteButton.visibility = View.GONE
        if (context is MainActivity) {
            val addButton : FloatingActionButton = context.findViewById(R.id.buttonAdd)
            addButton.visibility = View.VISIBLE
        }
    }

    /**
     * Refresh delete
     *
     * @param holder
     * @param fold
     *
     * On décoche toutes les checkbox
     */
    fun refreshDelete(holder: PromiseViewHolder) {
        holder.promise = promiseList.elementAt(holder.adapterPosition)
        holder.checkBox.isChecked = holder.promise.isChecked
        holder.checkBox.isVisible = inSelection
        //refreshState(holder)--------------------------------------------------------------------------

    }

    /**
     * Refresh state
     *
     * @param holder
     * Je refresh la promesse en fonction de son état pour le background de la promesse
     */
    fun refreshState(holder: PromiseViewHolder) {
        holder.promise = promiseList.elementAt(holder.adapterPosition)
        statePromiseUpdate(holder)
    }

    /**
     * Refresh promise
     *
     * @param holder
     * @param fold
     *
     * Mise à jour de la promesse en fonction du déploiement ou non de notre promesse
     * Dans le cas ou l'on déploie la promesse on anime le logo, le titre de la promesse et on
     * étire la promesse alors que dans l'autre cas on on lui fait retrouver sa forme originelle
     */
    private fun refreshPromise(holder: PromiseViewHolder) {

        holder.promise = promiseList.elementAt(holder.adapterPosition)
        var isdepl = holder.promise.isDescDeployed
        if (holder.promise.isDescDeployed) holder.titre.textSize = utils.endSize else holder.titre.textSize = utils.startSize
        improoveWidthLogo(holder)
        if (!isdepl){
            undeployAnimation(holder)
        }
        else{
            deployAnimation(holder)
        }

    }

    /**
     * Deploy animation
     *
     * @param holder
     *
     * Animation de déploiement
     */
    private fun deployAnimation(holder: PromiseViewHolder) {
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

        val animationDuration = 300 // Animation duration in ms
        val animator: ValueAnimator = ObjectAnimator.ofFloat(tv, "textSize", utils.startSize, utils.endSize)
        animator.duration = animationDuration.toLong()
        animator.start()
    }

    /**
     * Undeploy animation
     *
     * @param holder
     * Animation lorsque l'on rétrécie la promesse
     */
    private fun undeployAnimation(holder: PromiseViewHolder) {
        var animation = SlideAnimation(holder.deployed)
        animation.expand(holder.promise.isDescDeployed)
        val tv = holder.titre
        val animationDuration = 300 // Animation duration in ms
        val animator: ValueAnimator = ObjectAnimator.ofFloat(tv, "textSize", utils.endSize, utils.startSize)
        animator.duration = animationDuration.toLong()
        animator.start()
        holder.titre.animate()
        Handler().postDelayed({
            holder.deployed.visibility =
                if (holder.promise.isDescDeployed) View.VISIBLE else View.GONE
        }, 300)
    }

    /**
     * Improove width logo
     *
     * @param holder
     * Augmentation de la taille du logo afin de le rendre animé
     */
    private fun improoveWidthLogo(
        holder: PromiseViewHolder
    ) {
        if (holder.promise.isDescDeployed) {
            val animator1: ObjectAnimator = ObjectAnimator.ofInt(holder.logo,
                WidthProperty(),
                utils.currentWidthStart,
                utils.currentWidthEnd)
            animator1.duration = 300
            animator1.interpolator = DecelerateInterpolator()
            animator1.start()
        } else {
            val animator1: ObjectAnimator = ObjectAnimator.ofInt(holder.logo,
                WidthProperty(),
                utils.currentWidthEnd,
                utils.currentWidthStart)
            animator1.duration = 300
            animator1.interpolator = DecelerateInterpolator()
            animator1.start()
        }
    }

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_promise,
            parent,
            false)
        return PromiseViewHolder(itemView)
    }

    /**
     * Set margins
     *
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     *
     * Fonction pas au point présente dans le cas ou l'on souhaite affiher les promesses triées
     * por catégorie
     */
    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    /**
     * Restore item
     *
     * @param promise
     * @param position
     * @param dataBase
     *
     * Fonction qui permet la restoration d'un item
     * on le rajoute à la liste des promesse ainsi qu'à la base de donnée
     */
    fun restoreItem(promise: Promise, position: Int, dataBase: PromiseDataBase) {
        promiseList.add(promise)
        dataBase.updateDate(promise)
    }

    /**
     * On item move
     *
     * @param fromPosition
     * @param toPosition
     * Fonction appelée lorsque l'on bouge une promesse à l'aide du drag and drop
     *On échange les deux éléments de place, on les supprime et les rajoute à la liste des promesses
     * de l'utilisateur ainsi qu'à la base de données
     */
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
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

    /**
     * On item dismiss
     *
     * @param position
     *
     * Onitemdismiss supprime la promesse de la liste des promesses
     */
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
     * classe interne qui permet de relier toute la vue
     * On trouve tout les éléments présent XML dans l'affichage d'une promesse
     */// HOLDER
    inner class PromiseViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener,
        View.OnLongClickListener {
        lateinit var promise: Promise
        lateinit var savePromise: Promise
        var titre: TextView = view.findViewById(R.id.title)
        var recipient: TextView = view.findViewById(R.id.textViewUsernameRecipient)
        var logo: ImageView = view.findViewById(R.id.logo)
        var imageViewCategoryGlobal: ImageView = view.findViewById(R.id.imageViewCategoryGlobal)
        var date: TextView = view.findViewById(R.id.date)
        var description: TextView = view.findViewById(R.id.desc)
        var checkBox: CheckBox = view.findViewById(R.id.delCheckBox)
        var layout: LinearLayout = view.findViewById(R.id.linearlayoutitem)
        var layoutButtonEdit: ConstraintLayout = view.findViewById(R.id.layoutButtonEdit)
        var buttonEdit: Button = view.findViewById(R.id.buttonEdit)
        var buttonStart: Button = view.findViewById(R.id.buttonStart)
        var buttonRedo: Button = view.findViewById(R.id.buttonRedo)
        var buttonStop: Button = view.findViewById(R.id.buttonStop)
        var buttonDone: Button = view.findViewById(R.id.buttonDone)
        var textViewInProgress: TextView = view.findViewById(R.id.textViewInProgress)
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

        /**
         * On click
         *
         * @param v
         *
         * Fonction qui est appelée dans le cas ou l'on clique sur une promesse
         * Celle ci dispatche l'évènement en fonction de l'élément cliqué
         */
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
                            listener.onItemClick(posAdapter, this@PromiseAdapter)
                        }
                    }
                }
            }
        }

        /**
         * On long click
         *
         * @param v
         * @return
         *
         * Fonction appelée lors d'un clic long sur une promesse
         */
        override fun onLongClick(v: View?): Boolean {
            posAdapter = adapterPosition
            if (posAdapter != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(posAdapter, this@PromiseAdapter)
            }
            return true
        }

        /**
         * Start animation
         *
         * @param animation
         */
        fun startAnimation(animation: Animation) {
            super.itemView.startAnimation(animation)
        }
    }

    /**
     * On item click listener
     *
     * @constructor
     * Interface des events de la liste
     */
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

    /**
     * Width property
     *
     * Classe interne permettant de modifier la width du logo d'une promesse
     */
    internal class WidthProperty : Property<View, Int>(Int::class.java, "width") {
        override fun get(view: View): Int {
            return view.width
        }

        override fun set(view: View, value: Int?) {
            view.layoutParams.width = value!!
            view.layoutParams.height= value
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
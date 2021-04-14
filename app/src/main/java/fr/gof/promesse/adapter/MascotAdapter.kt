package fr.gof.promesse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Mascot

/**
 * Mascot adapter
 *
 * @property context
 * @property listMascot
 * @property listener
 * @property database
 * @property isUpdate si il est a false alors on est dans le cas de la sélection de la mascotte
 *                     pour la première fois sinon si il est à true c'est que l'on veut modifier
 *                     notre mascotte dans le profil
 * Nous avons créé un adapter (pour la catégorie des promesses) utilisé par notre recyclerView
 */
class MascotAdapter(
    var context: Context,
    var listMascot: List<Mascot>,
    val listener: OnItemClickListener,
    val database: PromiseDataBase,
    var isUpdate: Boolean = false
) : RecyclerView.Adapter<MascotAdapter.MyViewHolder>() {

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return viewHolder
     *
     * Tout dépend dans quel cas on se situe si on est dans le cas où l'on modifie notre mascotte
     * on aura un fond différent du cas où l'on choisit notre mascotte pour la première fois
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            if (isUpdate) LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mascot_update, parent, false)
            else LayoutInflater.from(parent.context).inflate(R.layout.item_mascot, parent, false)

        return MyViewHolder(itemView)
    }

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     *
     * fonction qui est appelée lorsque l'on recharge la vue de l'adapter
     * on change l'image de fond, le nom de la mascotte
     *
     * Dans le cas où isUpdate est à true on est dans la modification du profil
     * et donc on se sert d'un tag afin de pouvoir récupérer l'index
     * de la vue lors du scroll de la mascotte (dans l'activité de modification du profil)
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (isUpdate) holder.linearLayout?.tag = position
        holder.mascotView.setImageResource(listMascot[position].image_drawable)
        holder.name.text = (listMascot[position].nom)
    }

    /**
     * Get item count
     *
     * @return la taille de la liste des mascottes
     */
    override fun getItemCount(): Int {
        return listMascot.size
    }

    /**
     * My view holder
     *
     * @constructor
     *
     * @param itemView
     *
     * classe interne qui permet de récupérer les élements présents dans le XML. On récupère la vue
     * de notre mascotte, son nom, (et le layout dans le cas ou on se trouve dans le profil et que l'on souhaite
     * adapter la vue de la mascotte différemment de l'activité de sélection de la mascotte principale dans laquelle
     * on arrive lorsque l'on lance l'application pour la première fois)
     */
    inner class MyViewHolder(itemView: View) : View.OnClickListener,
        RecyclerView.ViewHolder(itemView) {
        var mascotView: ImageView = itemView.findViewById(R.id.mascotView)
        var name: TextView = itemView.findViewById(R.id.mascotName)
        var linearLayout: ConstraintLayout? = null

        init {
            if (isUpdate) linearLayout = itemView.findViewById(R.id.ll_item_mascot_update)
            itemView.setOnClickListener(this)
        }

        /**
         * On click
         *
         * @param v
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, this@MascotAdapter, database)
            }
        }
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
         * @param database
         */
        fun onItemClick(position: Int, adapter: MascotAdapter, database: PromiseDataBase)
    }
}

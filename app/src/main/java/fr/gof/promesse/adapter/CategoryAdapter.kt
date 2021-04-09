package fr.gof.promesse.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Category

/**
 * Category adapter
 *
 * @property context
 * @property listCategory
 * @property listener
 * @property database
 * @property backgroundImage
 * @property chosenCategory
 *
 * Nous avons créé un adapter pour la catégorie des promesses utilisé par notre recyclerView
 */
class CategoryAdapter(
    var context: Context,
    var listCategory: List<Category>,
    val listener: OnItemClickListener,
    val database: PromiseDataBase,
    var backgroundImage: ImageView,
    var chosenCategory: Category = Category.DEFAUT
) : RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // si la catégorie n'est pas sélectionné
        if (!listCategory[position].check) {
            holder.categoryView.setImageResource(listCategory[position].image_drawable)
            holder.name = (listCategory[position].nom)
        }
        // quand on sélectionne la catégorie on affiche un logo "check" a la place de l'image de catégorie
        else {
            holder.categoryView.setImageResource(R.drawable.selected)
            holder.name = (listCategory[position].nom)
            listCategory[position].check = false
        }
    }

    /**
     * Get item count
     *
     * @return la taille de la liste des catégories disponibles
     */
    override fun getItemCount(): Int {
        return listCategory.size
    }

    /**
     * My view holder
     *
     * @constructor
     *
     * @param itemView
     */
    inner class MyViewHolder(itemView: View) : View.OnClickListener,
        RecyclerView.ViewHolder(itemView) {
        var name: String = ""
        var categoryView: ImageView = itemView.findViewById(R.id.categoryView)

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * On click
         *
         * @param v vue
         *
         * quand on clique sur une catégorie on change l'image de fond on le coche et on décoche
         * l'ancien élément coché
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                chosenCategory = listCategory[position]
                backgroundImage.setImageResource(chosenCategory.background)
                listener.onItemClick(position, this@CategoryAdapter, database)
                listCategory[position].check = true
                if (!listCategory[position].check) {
                    categoryView.setImageResource(listCategory[position].image_drawable)
                    name = (listCategory[position].nom)
                } else {
                    for (elem in listCategory) {
                        if (elem.name != listCategory[position].name)
                            elem.check = false
                    }
                    categoryView.setImageResource(R.drawable.selected)
                    name = (listCategory[position].nom)
                }
                this@CategoryAdapter.notifyDataSetChanged()
            }
        }
    }


    /**
     * On item click listener
     *
     * @constructor Create empty On item click listener
     */
    interface OnItemClickListener {
        /**
         * On item click
         *
         * @param position
         * @param adapter
         * @param database
         */
        fun onItemClick(position: Int, adapter: CategoryAdapter, database: PromiseDataBase)
    }
}

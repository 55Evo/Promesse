
package fr.gof.promesse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Category
import fr.gof.promesse.model.Promise

/**
 * Category adapter
 *
 * @property context
 * @property listCategory
 * @property listener
 * @property database
 * @constructor Create empty Mascot adapter
 */
class CategoryAdapter(var context: Context, var listCategory: List<Category>, val listener : OnItemClickListener, val database : PromiseDataBase, var backgroundImage: ImageView) :RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

//    private val inflater: LayoutInflater
//    private val imageModelArrayList: ArrayList<Mascot>
    var chooenCategory : Category = Category.DEFAUT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.categoryView.setImageResource(listCategory[position].image_drawable)
        holder.name = (listCategory[position].nom)
    }

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
    inner class MyViewHolder(itemView: View) :  View.OnClickListener, RecyclerView.ViewHolder(itemView) {
        var name: String =""
        var categoryView: ImageView = itemView.findViewById(R.id.categoryView)

         init {
             itemView.setOnClickListener(this)
         }

         override fun onClick(v: View?) {
             val position = adapterPosition
             if (position != RecyclerView.NO_POSITION) {
                 chooenCategory = listCategory[position]
                 backgroundImage.setImageResource(chooenCategory.background)
                 listener.onItemClick(position, this@CategoryAdapter, database)

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
        fun onItemClick(position: Int, adapter : CategoryAdapter, database : PromiseDataBase)
    }
}


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
 * @constructor Create empty Mascot adapter
 */
class CategoryAdapter(var context: Context, var listCategory: List<Category>, val listener : OnItemClickListener, val database : PromiseDataBase,var backgroundImage : ImageView, var chosenCategory: Category = Category.DEFAUT) :RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

//    private val inflater: LayoutInflater
//    private val imageModelArrayList: ArrayList<Mascot>
      var saveCategory : Int = -1
      var save_img : Int = chosenCategory.background


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)



        return MyViewHolder(itemView)

    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        var test: CardView = holder.itemView.findViewById(R.id.card_view_category)
//        test.setCardBackgroundColor(R.color.green)
        if (!listCategory[position].check){
            holder.categoryView.setImageResource(listCategory[position].image_drawable)
            holder.name = (listCategory[position].nom)

        }
        else{
            holder.categoryView.setImageResource(R.drawable.selected)
            holder.name = (listCategory[position].nom)
            listCategory[position].check = false
        }



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
                 //this@CategoryAdapter.notifyDataSetChanged()
                 chosenCategory = listCategory[position]

                 backgroundImage.setImageResource(chosenCategory.background)
                // chosenCategory.background = R.drawable.cuisine
                // categoryView.setImageResource(R.drawable.cuisine)
                 listener.onItemClick(position, this@CategoryAdapter, database)
                 listCategory[position].check = true

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
        fun onItemClick(position: Int, adapter : CategoryAdapter, database : PromiseDataBase)
    }
}


package fr.gof.promesse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
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
 * @constructor Create empty Mascot adapter
 */
class MascotAdapter(var context: Context, var listMascot: List<Mascot>, val listener : OnItemClickListener, val database : PromiseDataBase, var isUpdate : Boolean = false) :RecyclerView.Adapter<MascotAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView =
                if (isUpdate) LayoutInflater.from(parent.context).inflate(R.layout.item_mascot_update, parent, false)
                else LayoutInflater.from(parent.context).inflate(R.layout.item_mascot, parent, false)

        return MyViewHolder(itemView)
    }

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.linearLayout.tag = position
        holder.mascotView.setImageResource(listMascot[position].image_drawable)
        holder.name.text = (listMascot[position].nom)
    }

    /**
     * Get item count
     *
     * @return
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
     */
    inner class MyViewHolder(itemView: View) :  View.OnClickListener, RecyclerView.ViewHolder(itemView) {
        var mascotView: ImageView = itemView.findViewById(R.id.mascotView)
         var name: TextView = itemView.findViewById(R.id.mascotName)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.ll_item_mascot_update)

         init {
             itemView.setOnClickListener(this)
         }

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
        fun onItemClick(position: Int, adapter : MascotAdapter, database : PromiseDataBase)
    }
}

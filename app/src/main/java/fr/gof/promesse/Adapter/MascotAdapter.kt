
package fr.gof.promesse.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Mascot
import java.util.*

class MascotAdapter(public var context: Context, var listMascot: List<Mascot>,val listener : MascotAdapter.OnItemClickListener,val database : PromiseDataBase) :RecyclerView.Adapter<MascotAdapter.MyViewHolder>() {

//    private val inflater: LayoutInflater
//    private val imageModelArrayList: ArrayList<Mascot>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mascot_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val promise = listMascot[position]
        holder.mascotView.setImageResource(listMascot[position].image_drawable)
        holder.name.text = (listMascot[position].name)
    }

    override fun getItemCount(): Int {
        return listMascot.size
    }

     inner class MyViewHolder(itemView: View) :  View.OnClickListener, RecyclerView.ViewHolder(itemView) {
        var mascotView: ImageView = itemView.findViewById(R.id.mascotView)
         var name: TextView = itemView.findViewById(R.id.mascotName)

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


    interface OnItemClickListener {
        fun onItemClick(position: Int, adapter : MascotAdapter, database : PromiseDataBase)
    }
}

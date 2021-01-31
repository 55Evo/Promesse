package fr.gof.promesse.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.R
import fr.gof.promesse.SearchActivity
import fr.gof.promesse.model.Promise

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    var date: TextView = itemView.findViewById(R.id.date)
    var description: TextView = itemView.findViewById(R.id.description)
    var layout:LinearLayout = itemView.findViewById(R.id.linearlayoutitem)

    var globalDesc : String =""
    var shortDesc : String =""
    var short = false

}
class SearchAdapter(var context: Context, var listePromesses :  List<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){
    lateinit var itemView : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.layoutsearchitems, parent, false)

        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        holder.globalDesc ="Description :" +listePromesses.get(position).description
        if (holder.globalDesc.lastIndex > 20){
            holder.shortDesc = holder.globalDesc.substring(0,20)+"... "
            holder.description.setText(holder.shortDesc)
            holder.short = true
        }
        else{
            holder.description.setText(holder.globalDesc)
        }
        holder.title.text = listePromesses[position].title
        holder.date.setText(listePromesses.get(position).dateTodo.toString())

        when(listePromesses[position].priority){
            true->holder.layout.setBackgroundResource(R.drawable.layout_border_important)
            false->holder.layout.setBackgroundResource(R.drawable.layout_border)
        }
        holder.layout.setPadding(20,20,20,20)



        itemView.setOnClickListener { v ->
            //On change d'activité (vers SearchActivity)
            val intent = Intent(context, PromiseManagerActivity::class.java)
            //context.startActivity(intent)
            when(holder.short){
                true-> {
                    holder.description.setText(holder.globalDesc)
                    holder.short = false
                }
                false->{
                    holder.description.setText(holder.shortDesc)
                    holder.short = true
                }
            }

            Toast.makeText(context, listePromesses[position].toString()+" sélectionné !" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

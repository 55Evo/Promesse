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
import java.io.Serializable

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    var date: TextView = itemView.findViewById(R.id.date)
    var description: TextView = itemView.findViewById(R.id.description)
    var layout:LinearLayout = itemView.findViewById(R.id.linearlayoutitem)

    var globalDesc : String =""
    var shortDesc : String =""
    var isDeployed = false

}
class SearchAdapter(var context: Context, var listePromesses :  List<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){
    lateinit var itemView : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.layoutsearchitems, parent, false)

        return SearchViewHolder(itemView)
    }
    fun displayDescription(holder: SearchViewHolder,position: Int){

        holder.globalDesc ="Description :\n" +listePromesses.get(position).description
        if (holder.globalDesc.lastIndex > 50){
            holder.shortDesc = holder.globalDesc.substring(0,50)+"... "
            holder.description.setText(holder.shortDesc)
            holder.isDeployed = true
        }
        else{
            holder.description.setText(holder.globalDesc)
        }
        holder.title.text = listePromesses[position].title
        holder.date.setText(listePromesses.get(position).dateTodo.toString())
    }
    fun setColorWithPriority(position : Int, holder: SearchViewHolder){
        when(listePromesses[position].priority){
            true->holder.layout.setBackgroundResource(R.drawable.layout_border_important)
            false->holder.layout.setBackgroundResource(R.drawable.layout_border)
        }
        holder.layout.setPadding(20,20,20,20)
    }
    fun deployDescription(holder: SearchViewHolder){
        if(holder.isDeployed)
         {
            holder.description.setText(holder.globalDesc)
            holder.isDeployed = false
        }
        else{
            holder.description.setText(holder.shortDesc)
            holder.isDeployed = true
        }

    }
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        displayDescription(holder,position)
        setColorWithPriority(position, holder)

        itemView.setOnClickListener { v ->
            //quand je clique sur un item

            // on check si on doit déploy ou rapetisser la description
            deployDescription(holder)
            // on affiche un toast

            Toast.makeText(context, listePromesses[position].title.toString()+" sélectionné !" , Toast.LENGTH_SHORT).show()
        }
        // quand on garde appuyé on arrive sur la page de modification de tache concernant notre promesse
        itemView.setOnLongClickListener { v ->
         val intent = Intent(context, PromiseManagerActivity::class.java)
            intent.putExtra("Promise",listePromesses[position] )
            context.startActivity(intent)
            true
        }
    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

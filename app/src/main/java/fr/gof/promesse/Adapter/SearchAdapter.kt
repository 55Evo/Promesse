package fr.gof.promesse.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var title : TextView = itemView.findViewById(R.id.title)
    var date : TextView = itemView.findViewById(R.id.date)
    var description : TextView = itemView.findViewById(R.id.description)
}

class SearchAdapter(var context: Context, var listePromesses :  List<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){
    lateinit var itemView : View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.layoutsearchitems, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.title.text = listePromesses[position].title
        holder.date.setText(listePromesses.get(position).dateTodo.toString())
        holder.description.setText(listePromesses.get(position).description)
        itemView.setOnClickListener { v ->
            Toast.makeText(context, listePromesses[position].toString()+" sélectionné !" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

package fr.gof.promesse.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise
import org.w3c.dom.Text


class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var title : TextView = itemView.findViewById(R.id.title)
    var date : TextView = itemView.findViewById(R.id.date)
    var description : TextView = itemView.findViewById(R.id.description)
}

class SearchAdapter(var context: Context, var listePromesses :  List<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        var itemView = inflater.inflate(R.layout.layoutsearchitems, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.title.text = listePromesses[position].title
        holder.date.setText(listePromesses.get(position).dateTodo.toString())
        holder.description.setText(listePromesses.get(position).description)

    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

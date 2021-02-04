package fr.gof.promesse.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginTop

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.R
import fr.gof.promesse.SearchActivity
import fr.gof.promesse.database.PromiseDataBase
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
class SearchAdapter(var context: Context, var listePromesses :  MutableList<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){
    lateinit var itemView : View
    val promiseDataBase = PromiseDataBase(context)

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
    override fun onBindViewHolder(holder: SearchViewHolder, pos: Int) {
        displayDescription(holder,pos)
        setColorWithPriority(pos, holder)

        itemView.setOnClickListener { v ->
            val position = (v.parent as RecyclerView).getChildLayoutPosition(v)
            //quand je clique sur un item
            notifyDataSetChanged()
            Log.d("list", listePromesses.toString())
            // on check si on doit déploy ou rapetisser la description
            deployDescription(holder)
            // on affiche un toast

            Toast.makeText(context, listePromesses[position].title.toString()+" sélectionné !" , Toast.LENGTH_SHORT).show()
        }
        // quand on garde appuyé on arrive sur la page de modification de tache concernant notre promesse
//        itemView.setOnLongClickListener { v ->
//         val intent = Intent(context, PromiseManagerActivity::class.java)
//            intent.putExtra("Promise",listePromesses[position] )
//            context.startActivity(intent)
//            true
//        }
        itemView.setOnLongClickListener { view ->
            val del = view.findViewById<ImageButton>(R.id.deleteButton2)
            val position = (view.parent as RecyclerView).getChildLayoutPosition(view)

            del.visibility = View.VISIBLE
            del.setOnClickListener(View.OnClickListener { view ->
                if (listePromesses[position].subtasks != null) {
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage("Attention, cette promesse possède des sous-tâches. " +
                            "Êtes-vous sûr(e) de vouloir la supprimer ?")
                        .setCancelable(true)
                        .setPositiveButton("Oui", DialogInterface.OnClickListener {
                                _, _ -> promiseDataBase.deletePromise(listePromesses.get(position))
                            listePromesses.removeAt(position)

                            notifyDataSetChanged()
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, listePromesses.size)
                            Log.d("pos", position.toString())
                            Log.d("size", listePromesses.size.toString())
                            del.visibility = View.INVISIBLE
                        })
                        .setNegativeButton("Non", DialogInterface.OnClickListener {
                                dialog, _ -> dialog.cancel()
                        })

                    val alert = dialogBuilder.create()
                    alert.setTitle("Suppression de promesse")
                    alert.show()

                }
                else {
                    promiseDataBase.deletePromise(listePromesses.get(position))
                    listePromesses.removeAt(position)
                    notifyDataSetChanged()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, listePromesses.size)
                    Log.d("pos", position.toString())
                    Log.d("size", listePromesses.size.toString())
                    del.visibility = View.INVISIBLE
                }
            })

            true}
    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

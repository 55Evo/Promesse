package fr.gof.promesse.Adapter

import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import java.util.concurrent.TimeUnit


class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    var date: TextView = itemView.findViewById(R.id.date)
    var description: TextView = itemView.findViewById(R.id.description)
    var layout:LinearLayout = itemView.findViewById(R.id.linearlayoutitem)
    var case:CheckBox = itemView.findViewById<CheckBox>(R.id.delCheckBox)

    var globalDesc : String =""
    var shortDesc : String =""
    var isDeployed = false
}
class SearchAdapter(var context: Context, var listePromesses: MutableList<Promise>) : RecyclerView.Adapter<SearchViewHolder>(){

    val promiseDataBase = PromiseDataBase(context)
    var isSelect = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        var itemView : View = inflater.inflate(R.layout.layoutsearchitems, parent, false)
        var holder = SearchViewHolder(itemView)
        holder.case.visibility = View.INVISIBLE

        itemView.setOnClickListener { v ->
            val position = (v.parent as RecyclerView).getChildLayoutPosition(v)
            //quand je clique sur un item
            Log.d("coucouuuuuuuuuuuuuuuuuuuuuuuuu", position.toString())
            // on check si on doit déploy ou rapetisser la description
            deployDescription(holder)

            val description : TextView = v.findViewById<TextView>(R.id.description)

//            if(description.maxLines == 10) v.findViewById<TextView>(R.id.description).maxLines = 2
//            else v.findViewById<TextView>(R.id.description).maxLines = 10

           // displayItem(holder,position)
            // on affiche un toast
            Toast.makeText(context, listePromesses[position].title.toString() + " sélectionné !", Toast.LENGTH_SHORT).show()
        }
        // quand on garde appuyé on arrive sur la page de modification de tache concernant notre promesse
//        itemView.setOnLongClickListener { v ->
//         val intent = Intent(context, PromiseManagerActivity::class.java)
//            intent.putExtra("Promise",listePromesses[position] )
//            context.startActivity(intent)
//            true
//        }

        itemView.setOnLongClickListener { view ->
            isSelect = true
            //Auto-check the item we clicked on
            holder.case.isChecked = true
            val parent = view.parent.parent as View
            val listView: RecyclerView = parent.findViewById(R.id.recycler_search)
            listView.setHasFixedSize(false)
            val delButton = parent.findViewById<FloatingActionButton>(R.id.deleteButton)
            //Show delete button
            delButton.visibility = View.VISIBLE
            //Show all checkboxes
            showHideCheckBoxes(listView, View.VISIBLE)

            view.findViewById<CheckBox>(R.id.delCheckBox).isChecked = true
            delButton.setOnClickListener(View.OnClickListener { view ->

                for (item in listView) {
                    val position = listView.getChildLayoutPosition(item)
                    //If the item is checked, must be deleted
                    if (item.findViewById<CheckBox>(R.id.delCheckBox).isChecked) {
                        //If the item has subtasks, but ask confirmation
                        if (listePromesses[position].subtasks != null) {
                            val dialogBuilder = AlertDialog.Builder(context)
                            dialogBuilder.setMessage("Attention, cette promesse possède des sous-tâches. " +
                                    "Êtes-vous sûr(e) de vouloir la supprimer ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Oui", DialogInterface.OnClickListener { _, _ ->
                                        deletePromise(position)
                                        delButton.visibility = View.INVISIBLE
                                    })
                                    .setNegativeButton("Non", DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.cancel()
                                    })

                            val alert = dialogBuilder.create()
                            alert.setTitle("Suppression de promesse")
                            alert.show()

                        }
                        //If the item has no subtasks, directly delete
                        else {
                            Log.d("position-------------", position.toString())
                            deletePromise(position)

                        }
                    }

                }
                //Hide delete button
                delButton.visibility = View.INVISIBLE
                //Hide checkboxes
                showHideCheckBoxes(listView, View.INVISIBLE)
            })

            true}




        return holder
    }
    private fun deletePromise(position: Int) {
        promiseDataBase.deletePromise(listePromesses.get(position))
        listePromesses.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listePromesses.size)
        //notifyDataSetChanged()
    }

    private fun showHideCheckBoxes(listView: RecyclerView, visibility: Int) {
            Log.d("sizzzzzzzeeeeeeeee", listePromesses.size.toString())
        Log.d("sizzzzzzzeeeeeeeeeuuuuuuuuuuuuuuuuuuuuuuu", listView.childCount.toString())


        for (item in listView) {
            Log.d("------------", listView.size.toString())
            item.findViewById<CheckBox>(R.id.delCheckBox).visibility = visibility
            item.findViewById<CheckBox>(R.id.delCheckBox).isChecked = false
        }
    }
    fun displayItem(holder: SearchViewHolder, position: Int){

        holder.globalDesc ="Description :\n" +listePromesses.get(position).description
        if (holder.globalDesc.lastIndex > 50){
            holder.shortDesc = holder.globalDesc.substring(0, 50)+"... "
            holder.description.setText(holder.shortDesc)
            holder.isDeployed = true
        }
        else{
            holder.description.setText(holder.globalDesc)
        }
//        if (isSelect){
//            holder.case.visibility = View.VISIBLE
//        }
//        else{
//            holder.case.visibility = View.INVISIBLE
//        }
        holder.title.text = listePromesses[position].title
        holder.date.setText(listePromesses.get(position).dateTodo.toString())
        //if (!holder.isDeployed) holder.description.maxLines = 2
        setColorWithPriority(position, holder)

        Log.d("ccccccccccccccccccccccccccccccccccccccccccccc", "oooooooooooooooooooooooooo")
    }

    fun setColorWithPriority(position: Int, holder: SearchViewHolder){
        when(listePromesses[position].priority){
            true -> holder.layout.setBackgroundResource(R.drawable.layout_border_important)
            false -> holder.layout.setBackgroundResource(R.drawable.layout_border)
        }

        holder.layout.setPadding(20, 20, 20, 20)

    }
//    fun deployDescription(holder: SearchViewHolder, position: Int){
//        if (holder.isDeployed){
//            holder.description.maxLines = 2
//            holder.isDeployed = false
//        }
//        else{
//            holder.description.maxLines = 10
//            holder.isDeployed = true
//        }
//    }
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

    fun TextView.addFilter(filter: InputFilter) {
        filters = if (filters.isNullOrEmpty()) {
            arrayOf(filter)
        } else {
            filters.toMutableList()
                    .apply {
                        removeAll { it.javaClass == filter.javaClass }
                        add(filter)
                    }
                    .toTypedArray()
        }
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        //val position = (holder.itemView.parent as RecyclerView).getChildLayoutPosition(holder.itemView)
        //holder.setIsRecyclable(false);
        displayItem(holder, position)



    }

    override fun getItemCount(): Int {
        return listePromesses.size
    }

}

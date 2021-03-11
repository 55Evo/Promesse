package fr.gof.promesse.listener

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.MainActivity
import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise

/**
 * Promise event listener
 *
 * @property listPromesses
 * @property context
 * @constructor Create empty Promise event listener
 */
class PromiseEventListener (var listPromesses : MutableList<Promise>, var context : Activity) : PromiseAdapter.OnItemClickListener {

    override fun onItemClick(position: Int, adapter : PromiseAdapter) {
        val clickedItem = listPromesses[position]


        clickedItem.isDescDeployed = !clickedItem.isDescDeployed
        //adapter.notifyItemChanged(position)


    }

    override fun onItemLongClick(position: Int, adapter : PromiseAdapter) {
        val clickedItem = listPromesses[position]
        if(!adapter.inSelection){
            clickedItem.isChecked = true
            adapter.nbPromisesChecked++
            adapter.inSelection = true
            adapter.notifyDataSetChanged()
            val deleteButton : FloatingActionButton = context.findViewById(R.id.deleteButton)
            deleteButton.visibility = View.VISIBLE
            if (context is MainActivity) {
                val addButton : FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.GONE
            }
        } else {
            uncheckItem(clickedItem, adapter)
        }
    }

    override fun onItemButtonEditClick(position: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = promiseAdapter.promiseList[position]
        Log.d("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm",clickedItem.id.toString() + "  " +clickedItem.dateCreation.toString()+"  "+clickedItem.category.toString()
        + clickedItem.description)

        var p = clickedItem.copy()

        val intent = Intent(context, PromiseManagerActivity::class.java)
        intent.putExtra("Promise", p)
        context.startActivity(intent)
    }

    override fun onItemCheckedChanged(position: Int, adapter: PromiseAdapter) {
        val clickedItem = listPromesses[position]
        uncheckItem(clickedItem, adapter)
    }

    private fun uncheckItem(clickedItem: Promise, adapter: PromiseAdapter) {
        if (clickedItem.isChecked) adapter.nbPromisesChecked--
        else adapter.nbPromisesChecked++
        clickedItem.isChecked = !clickedItem.isChecked
        if (adapter.nbPromisesChecked == 0) {
            adapter.inSelection = false
            val deleteButton: FloatingActionButton = context.findViewById(R.id.deleteButton)

            deleteButton.visibility = View.GONE


            if (context is MainActivity) {
                val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.VISIBLE
            }
        }
        //adapter.notifyDataSetChanged()
    }
}


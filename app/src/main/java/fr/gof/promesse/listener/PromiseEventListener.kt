package fr.gof.promesse.listener

import android.app.Activity
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise

class PromiseEventListener (var listPromesses : MutableList<Promise>, var context : Activity) : PromiseAdapter.OnItemClickListener {

    override fun onItemClick(position: Int, adapter : PromiseAdapter) {
        val clickedItem = listPromesses[position]
        clickedItem.isDescDeployed = !clickedItem.isDescDeployed
        adapter.notifyItemChanged(position)
    }

    override fun onItemLongClick(position: Int, adapter : PromiseAdapter) {
        val clickedItem = listPromesses[position]
        clickedItem.isChecked = true
        adapter.inSelection = true
        adapter.notifyDataSetChanged()
        val deleteButton : FloatingActionButton = context.findViewById(R.id.deleteButton)
        deleteButton.visibility = View.VISIBLE
    }

}

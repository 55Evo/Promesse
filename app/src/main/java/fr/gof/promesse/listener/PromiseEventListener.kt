package fr.gof.promesse.listener

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.gof.promesse.MainActivity
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.PromiseManagerActivity
import fr.gof.promesse.R
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.adapter.SubtaskAdapter
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State

/**
 * Promise event listener
 *
 * @property listPromesses
 * @property context
 * @constructor Create empty Promise event listener
 */
class PromiseEventListener(var listPromesses: MutableList<Promise>, var context: Activity) : PromiseAdapter.OnItemClickListener {

    override fun onItemClick(position: Int, adapter: PromiseAdapter) {
        val clickedItem = listPromesses[position]
        clickedItem.isDescDeployed = !clickedItem.isDescDeployed
        var bundle = Bundle()
        bundle.putBoolean("click", true)
        adapter.notifyItemChanged(position, bundle);
    }
    fun uncheckitems(adapter: PromiseAdapter){
        var bundle = Bundle()
        bundle.putBoolean("longclick", true)
        for (i in 0..(listPromesses.size)){
            adapter.notifyItemChanged(i, bundle);
        }
    }

    override fun onItemLongClick(position: Int, adapter: PromiseAdapter) {
        var clickedItem = listPromesses[position]
        if(!adapter.inSelection){
            clickedItem.isChecked = true
            adapter.nbPromisesChecked++
            adapter.inSelection = true

            //adapter.notifyItemChanged(position)
            Log.d("_______________________1__________________________________________oooo", "la")
            val deleteButton : FloatingActionButton = context.findViewById(R.id.deleteButton)
            deleteButton.visibility = View.VISIBLE
            if (context is MainActivity) {
                val addButton : FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.GONE
            }
            var bundle = Bundle()
            bundle.putBoolean("longclick", true)
            for (i in 0..(listPromesses.size)){
                adapter.notifyItemChanged(i, bundle);
            }

        } else {
            Log.d("___________________nnnon______________________________________________oooo", "la")
            uncheckItem(clickedItem, adapter)
        }
    }

    override fun onItemButtonEditClick(position: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = promiseAdapter.promiseList[position]
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
        Log.d("Nbpromessescheck", adapter.nbPromisesChecked.toString())
        clickedItem.isChecked = !clickedItem.isChecked
        if (adapter.nbPromisesChecked == 0) {
            adapter.inSelection = false
            val deleteButton: FloatingActionButton = context.findViewById(R.id.deleteButton)
            deleteButton.visibility = View.GONE
            if (context is MainActivity) {
                val addButton: FloatingActionButton = context.findViewById(R.id.buttonAdd)
                addButton.visibility = View.VISIBLE
            }
            uncheckitems(adapter)
        }
        else{

        var bundle = Bundle()
        bundle.putBoolean("longclick", true)
            adapter.notifyItemChanged(adapter.promiseList.lastIndexOf(clickedItem), bundle);

        //adapter.notifyItemChanged(adapter.promiseList.lastIndexOf(clickedItem))
        }

    }

    override fun onCheckSubtaskChanged(
            position: Int,
            promise: Promise,
            subtaskAdapter: SubtaskAdapter,
            promiseAdapter: PromiseAdapter
    ) {
        var clickedItem = subtaskAdapter.subtaskList[position]
        clickedItem.done = !clickedItem.done
        user.updateDoneSubtask(clickedItem, clickedItem.done)
        //promiseAdapter.notifyItemChanged(position)
        var bundle = Bundle()
        bundle.putBoolean("clicksubtask", true)
        promiseAdapter.notifyItemChanged(promiseAdapter.promiseList.lastIndexOf(promise), bundle)
    }

    override fun onItemButtonStartClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses[posAdapter]
        clickedItem.state = State.IN_PROGRESS
        user.updatePromise(clickedItem)
        promiseAdapter.notifyItemChanged(posAdapter)
    }

    private fun planeMode(enable: Boolean) {
        // Checking if permission is not granted
        if (context.checkSelfPermission("com.android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_DENIED) {
            context.requestPermissions(arrayOf("com.android.permission.WRITE_SECURE_SETTINGS") , 0);
        }
        val isPlaneMode = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON) == 1
        if (enable) {
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (isPlaneMode) 0 else 1)
        } else {
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (!isPlaneMode) 1 else 0)
        }

        val intent: Intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intent.putExtra("state", !isPlaneMode)
        context.sendBroadcast(intent)
    }

    override fun onItemButtonStopClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses[posAdapter]
        clickedItem.state = State.TODO
        user.updatePromise(clickedItem)
        promiseAdapter.notifyItemChanged(posAdapter)
    }

    override fun onItemButtonRedoClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses[posAdapter]
        clickedItem.state = State.TODO
        user.updatePromise(clickedItem)
        promiseAdapter.notifyItemChanged(posAdapter)
    }

    override fun onItemButtonDoneClick(posAdapter: Int, promiseAdapter: PromiseAdapter) {
        var clickedItem = listPromesses[posAdapter]
        clickedItem.state = State.DONE
        user.updatePromise(clickedItem)
        promiseAdapter.notifyItemChanged(posAdapter)
    }
}


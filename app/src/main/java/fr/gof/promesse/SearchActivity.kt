package fr.gof.promesse
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import fr.gof.promesse.Adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.*
import java.util.*


class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    lateinit var listPromesses: MutableList<Promise>
    var promiseDataBase = PromiseDataBase(this@SearchActivity)
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : PromiseAdapter
    lateinit var materialSearchBar : MaterialSearchBar
    lateinit var listSuggestions : MutableSet<Promise>
    lateinit var defaultUser : User
    var choiceOfSort : Sort = Sort.NAME
    var valeurActuelle : String =""
    lateinit var deleteButton : FloatingActionButton
    lateinit var deleteListner : DeleteButtonListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        utils.user = promiseDataBase.createDefaultAccount(Mascot("Super mascotte", R.drawable.mascot1, R.drawable.mascot_afficher_1))



        recyclerView  = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        deleteButton = findViewById(R.id.deleteButton)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        materialSearchBar.setCardViewElevation(10)
        materialSearchBar.setPlaceHolder("Bonjour "+utils.user.name+" !")

        loadSuggestList()
        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (materialSearchBar.text.length != 0) {

                    val suggest = mutableListOf<String>()
                    for (search in listSuggestions) {
                        if (search.title.toLowerCase().contains(materialSearchBar.text.toLowerCase())) {
                            suggest.add(search.title);
                        }
                        materialSearchBar.lastSuggestions = suggest
                    }
                }
            }
        })
        materialSearchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemDeleteListener(position: Int, v: View?) {
                listSuggestions.remove(listSuggestions.elementAt(position))
                var list = getTitleOfListSuggestion()
                if(listSuggestions.size>20) materialSearchBar.lastSuggestions = list.subList(0,20)
                else materialSearchBar.lastSuggestions = list
            }
            override fun OnItemClickListener(position: Int, v: View?) {
                //hideKeyboard(this@SearchActivity)
                startResearch(materialSearchBar.lastSuggestions[position].toString())
            }
        })
        materialSearchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    recyclerView.adapter = adapter
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startResearch(text.toString())
            }
            override fun onButtonClicked(buttonCode: Int) {
            }
        })

        listPromesses = utils.user.getSearchResultsSorted("", choiceOfSort, promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this))

        deleteListner = DeleteButtonListener(adapter, this, promiseDataBase)
        deleteButton.setOnClickListener(deleteListner)
        this.recyclerView.adapter = adapter
    }

    private fun startResearch(text: String, relaunch : Boolean = true) {
        if (relaunch)
        {
            materialSearchBar.closeSearch()
            materialSearchBar.setPlaceHolder(text)
        }

        valeurActuelle = text
        listPromesses = utils.user.getSearchResultsSorted(text, choiceOfSort, promiseDataBase).toMutableList()
        deleteButton.visibility = View.INVISIBLE

        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this))
        deleteListner.adapter = adapter
        recyclerView.adapter = adapter
        hideKeyboard(this)

    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getTitleOfListSuggestion() : MutableList<String>{
        var liste = mutableListOf<String>()
        for (e in listSuggestions) liste.add(e.title)
        return liste
    }
    private fun loadSuggestList() {
        listSuggestions = promiseDataBase.getAllPromises() as MutableSet<Promise>
        var list = getTitleOfListSuggestion()
        if(listSuggestions.size>20) materialSearchBar.lastSuggestions = list.subList(0,20)
        else materialSearchBar.lastSuggestions = list
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menudate -> choiceOfSort = Sort.DATE
            R.id.menupriority -> choiceOfSort = Sort.PRIORITY
            R.id.menuname -> choiceOfSort = Sort.NAME
        }
        startResearch(valeurActuelle, false)
        return true
    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        super.onResume()
    }
    fun onAddButtonClicked (v : View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

}


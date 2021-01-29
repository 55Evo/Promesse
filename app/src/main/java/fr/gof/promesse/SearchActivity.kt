package fr.gof.promesse
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import fr.gof.promesse.Adapter.SearchAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.Sort
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*


class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    var promiseDataBase = PromiseDataBase(this@SearchActivity)
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : SearchAdapter
    lateinit var materialSearchBar : MaterialSearchBar
    lateinit var listSuggestions : Set<Promise>
    lateinit var defaultUser : User
    var choiceOfSort : Sort = Sort.DATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        defaultUser = promiseDataBase.createDefaultAccount()
        val label = findViewById<TextView>(R.id.test)
        val promesse = Promise(-1, "Promesse2 priorité", 5, State.DONE, true, "Desc", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promesse, promiseDataBase)
        val promesse1 = Promise(-1, "Promesse0", 5, State.TODO, false, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse1, promiseDataBase)
        val promesse2 = Promise(-1, "Promesse1", 5, State.TODO, false, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()+200), null)
        defaultUser.addPromise(promesse2, promiseDataBase)
        val promesse3 = Promise(-1, "Promesse3 priorité", 5, State.TODO, true, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse3, promiseDataBase)
        Log.d("TAG","--------------------" +" coucouuuuuuuuuuuu")

        recyclerView  = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        materialSearchBar.setCardViewElevation(10)
        loadSuggestList()



        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (materialSearchBar.text.length != 0){

                    val suggest = mutableListOf<String>()
                    for (search in listSuggestions)  {

                        if (search.title.toLowerCase().contains(materialSearchBar.text.toLowerCase()))
                        {
                            suggest.add(search.title);
                        }
                        materialSearchBar.lastSuggestions = suggest

                    }
                }
            }
        })
        materialSearchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemDeleteListener(position: Int, v: View?) {
            }

            override fun OnItemClickListener(position: Int, v: View?) {

                startResearch(materialSearchBar.lastSuggestions[position].toString())
                hideKeyboard(this@SearchActivity)
                var search = materialSearchBar.lastSuggestions[position].toString()
                materialSearchBar.text = search
                materialSearchBar.closeSearch()
                //materialSearchBar.setPlaceHolder(search)
            }

        })
        materialSearchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if(!enabled) {recyclerView.adapter = adapter}
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startResearch(text.toString())
            }

            override fun onButtonClicked(buttonCode: Int) {
                TODO("Not yet implemented")
            }
        })

        adapter = SearchAdapter(this,promiseDataBase.getAllPromises().toList())
        this.recyclerView.adapter = adapter
    }

    private fun startResearch(text: String) {
        adapter = SearchAdapter(this, defaultUser.getSearchResultsSorted(text, choiceOfSort, promiseDataBase).toList())
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

    private fun loadSuggestList() {
        listSuggestions = promiseDataBase.getAllPromises()
        var liste = mutableListOf<String>()
        for (e in listSuggestions) liste.add(e.title)
        materialSearchBar.lastSuggestions = liste
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menudate -> choiceOfSort = Sort.DATE
            R.id.menupriority -> choiceOfSort = Sort.PRIORITY
            R.id.menuname -> choiceOfSort = Sort.NAME
        }
        startResearch(materialSearchBar.text.toString())
        return true
    }

}


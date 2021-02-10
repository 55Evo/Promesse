package fr.gof.promesse
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mancj.materialsearchbar.MaterialSearchBar
import fr.gof.promesse.adapter.CustomSuggestionAdapter
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.*


class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, CustomSuggestionAdapter.OnItemClickListener {

    lateinit var customSuggestionAdapter: CustomSuggestionAdapter
    lateinit var listPromesses: MutableList<Promise>
    var promiseDataBase = PromiseDataBase(this@SearchActivity)
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : PromiseAdapter
    lateinit var materialSearchBar : MaterialSearchBar
    lateinit var listSuggestions : MutableSet<Promise>
    var choiceOfSort : Sort = Sort.NAME
    var valeurActuelle : String = ""
    lateinit var deleteButton : FloatingActionButton
    lateinit var deleteListener : DeleteButtonListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView  = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        deleteButton = findViewById(R.id.deleteButton)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        //materialSearchBar.setCardViewElevation(10)
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),utils.user.name))
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customSuggestionAdapter = CustomSuggestionAdapter(layoutInflater, this)

        customSuggestionAdapter.suggestions = promiseDataBase.getAllPromises().toList()
        materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)

        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (materialSearchBar.text.length > before) {
                    materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
                    val suggest = mutableListOf<Promise>()

                    for (search in customSuggestionAdapter.suggestions) {
                        if (search.title.toLowerCase().contains(materialSearchBar.text.toLowerCase())) {
                            suggest.add(search)
                        }
                        materialSearchBar.lastSuggestions = suggest
                    }
                } else {
                    customSuggestionAdapter.suggestions = promiseDataBase.getAllPromises().toList()
                    materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
                }
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

        deleteListener = DeleteButtonListener(adapter, this, promiseDataBase)
        deleteButton.setOnClickListener(deleteListener)
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
        deleteListener.adapter = adapter
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
        super.onResume()
        listPromesses = utils.user.getAllPromise(promiseDataBase).toMutableList()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this))
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        customSuggestionAdapter.suggestions = promiseDataBase.getAllPromises().toList()
        materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),utils.user.name))
    }

    fun onAddButtonClicked (v : View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClick(v: View?) {
        var text = (v as TextView).text.toString()
        startResearch(text)
    }

}


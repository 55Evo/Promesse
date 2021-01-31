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
    var choiceOfSort : Sort = Sort.NAME
    var valeurActuelle : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        defaultUser = promiseDataBase.createDefaultAccount()
        val label = findViewById<TextView>(R.id.test)
        val promesse = Promise(-1, "faire l'amour", 5, State.DONE, true, "Ceci est la description de ce que faire l'amour signifie, \n cela signifie que pour réussir il faut aimer et pour aimer il faut avoir ", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promesse, promiseDataBase)
        val promessee = Promise(-1, "passer le permis", 5, State.DONE, true, "avoir le permis quelle belle ambition mais on ne sait pas si elle se réalisera un jour tellement tu n'es pas doué mon pauvre... on va quand meme essayer meme si cela sera dur", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promessee, promiseDataBase)
        val promesse1 = Promise(-1, "faire dodo", 5, State.TODO, false, "Dormir un bien grand mot bien plus grand que le mot sage par exemple", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse1, promiseDataBase)
        val promesse2 = Promise(-1, "faire des enfants", 5, State.TODO, false, "avoir des enfants s'en occuper.. Cela nest pas donne a tout le monde beaucoup les abandonnent", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis() + 200), null)
        defaultUser.addPromise(promesse2, promiseDataBase)
        val promesse3 = Promise(-1, "faire des études", 5, State.TODO, true, "faire des etudes je ne connais pas j'ai toujours ete chaumeur", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse3, promiseDataBase)
        Log.d("TAG", "--------------------" + " coucouuuuuuuuuuuu")

        recyclerView  = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        materialSearchBar.setCardViewElevation(10)
        materialSearchBar.setPlaceHolder("Bonjour "+defaultUser.name+" !")


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
            }

            override fun OnItemClickListener(position: Int, v: View?) {


                //hideKeyboard(this@SearchActivity)
                startResearch(materialSearchBar.lastSuggestions[position].toString())




                Log.d("TAG", "--------------------" + " okkkkkk")

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
                TODO("Not yet implemented")
            }
        })

        adapter = SearchAdapter(this, promiseDataBase.getAllPromises().toList())
        this.recyclerView.adapter = adapter
    }

    private fun startResearch(text: String, relaunch : Boolean = true) {
        if (relaunch)
        {
            materialSearchBar.closeSearch()
            materialSearchBar.setPlaceHolder(text)
        }

        valeurActuelle = text
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
        startResearch(valeurActuelle, false)
        return true
    }

}


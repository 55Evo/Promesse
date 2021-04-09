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
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.CustomSuggestionAdapter
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.*
import java.util.*

/**
 * Search activity
 *
 */
class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener,
    CustomSuggestionAdapter.OnItemClickListener {

    private lateinit var customSuggestionAdapter: CustomSuggestionAdapter
    private lateinit var listPromesses: TreeSet<Promise>
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PromiseAdapter
    lateinit var materialSearchBar: MaterialSearchBar
    var choiceOfSort: Sort = Sort.NAME
    private var valeurActuelle: String = ""
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var deleteListener: DeleteButtonListener
    private lateinit var slidr: SlidrInterface

    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        slidr = Slidr.attach(this, utils.config)
        setContentView(R.layout.activity_search)
        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        deleteButton = findViewById(R.id.deleteButton)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        materialSearchBar.setPlaceHolder(
            String.format(
                getString(R.string.searchbarPlaceholder),
                user.name
            )
        )
        listPromesses = user.getAllPromise()
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customSuggestionAdapter = CustomSuggestionAdapter(layoutInflater, this, this)
        customSuggestionAdapter.suggestions = listPromesses.toMutableList()
        materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (start > 0) {
                    val suggest =
                        user.getSearchResultsSorted(
                            (materialSearchBar.text.toLowerCase(Locale.ROOT)),
                            choiceOfSort
                        ).toMutableList()

                    if (suggest.size < 1) {
                        materialSearchBar.hideSuggestionsList()
                    } else {
                        materialSearchBar.updateLastSuggestions(suggest)
                    }
                }
            }
        })

        materialSearchBar.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {
            /**
             * On search state changed called when user type something in the
             * searchBar.
             * It updates the recyclerView adapter.
             *
             * @param enabled
             *
             * Méthode appelée quand un utilisateur tape quelque chose dans
             * la barre de recherche.
             * Permet de mettre à jour l'adapter du recyclerView.
             */
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    recyclerView.adapter = adapter
                }
            }

            /**
             * On search confirmed called when the searchButton is pressed.
             * It make a search of what was entered in the searchBar.
             *
             * @param text
             *
             * Méthode appelée lorsqu'on valide la recherche.
             * Permet de lancer la recherche en fonction de ce qui est
             * entré dans la barre de recherche.
             */
            override fun onSearchConfirmed(text: CharSequence?) {
                startResearch(text.toString())
            }

            /**
             * On button clicked
             *
             * @param buttonCode
             */
            override fun onButtonClicked(buttonCode: Int) {
            }
        })
    }

    /**
     * On resume called when activity is called again.
     * It refresh the view.
     *
     * Méthode appelée quand une activité est ouverte de nouveau.
     * Elle permet de mettre à jour la vue.
     *
     */
    override fun onResume() {
        super.onResume()
        listPromesses = user.getAllPromise()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this), this)
        deleteListener = DeleteButtonListener(adapter, this)
        deleteButton.setOnClickListener(deleteListener)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        materialSearchBar.setPlaceHolder(
            String.format(
                getString(R.string.searchbarPlaceholder),
                user.name
            )
        )
    }

    /**
     * Lock slider method that lock the back slide
     *
     * Méthode qui permet de bloquer le retour arrière
     * via le slide
     *
     */
    private fun lockSlider() {
        slidr.lock()
    }

    /**
     * Un lock slider method that unlock the back slide
     *
     * Méthode qui permet de débloquer le retour arrière
     * via le slide
     *
     */
    private fun unLockSlider() {
        slidr.unlock()
    }

    /**
     * Start research called when user confirm research.
     * It makes a search by the text in the searchBar and
     * update the view.
     *
     * @param text
     * @param relaunch
     *
     * Méthode appelée lorsque l'utilisateur confirme sa recherche.
     * Elle permet de démarrer une recherche en fonction du texte écrit
     * dans la barre de recherche. Elle met à jour la vue.
     */
    private fun startResearch(text: String, relaunch: Boolean = true) {
        if (relaunch) {
            materialSearchBar.closeSearch()
            materialSearchBar.setPlaceHolder(text)
        }

        valeurActuelle = text
        listPromesses = user.getSearchResultsSorted(text, choiceOfSort)
        deleteButton.visibility = View.INVISIBLE
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this), this)
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        materialSearchBar.clearSuggestions()
        materialSearchBar.hideSuggestionsList()
        hideKeyboard(this)
        listPromesses = user.getAllPromise()
        customSuggestionAdapter.suggestions = listPromesses.toMutableList()
        materialSearchBar.closeSearch()
    }

    /**
     * Hide keyboard when it's called.
     *
     * @param activity
     *
     * Cache le clavier quand on l'appelle.
     */
    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * On menu item click called when user click on sortDateButton,
     * sortPriorityButton or sortNameButton.
     * It updates the choiceOfSort to sort promises by date, name or priority.
     *
     * @param item
     * @return true
     *
     * Méthode appelée quand on appuie sur un des boutons de tri.
     * Elle permet de mettre à jour choiceOfSort pour pouvoir
     * trier les promesses par date, priorité ou nom.
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menudate -> choiceOfSort = Sort.DATE
            R.id.menupriority -> choiceOfSort = Sort.PRIORITY
            R.id.menuname -> choiceOfSort = Sort.NAME
        }
        startResearch(valeurActuelle, false)
        return true
    }

    /**
     * On add button clicked called when addButton is pressed.
     * It open promiseManagerActivity.
     *
     * @param v
     *
     * Méthode appelée quand on appuie sur le bouton d'ajout de
     * promesse.
     * Elle ouvre l'activité de création de promesse.
     */
    fun onAddButtonClicked(v: View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

    /**
     * On item click called when user click on a promise.
     * It deploy and display more informations about a promise.
     *
     * @param v
     *
     * Méthode appelée quand on clique sur une promesse.
     * Elle la déploie et affiche plus d'informations à son propos.
     */
    override fun onItemClick(v: View?) {
        val text = (v as TextView).text.toString()
        startResearch(text)
    }

}


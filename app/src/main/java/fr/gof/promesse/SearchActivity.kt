package fr.gof.promesse
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrPosition
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.CustomSuggestionAdapter
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.DeleteButtonListener
import fr.gof.promesse.model.*
import java.util.*


class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, CustomSuggestionAdapter.OnItemClickListener {

    lateinit var customSuggestionAdapter: CustomSuggestionAdapter
    lateinit var listPromesses: TreeSet<Promise>
    var promiseDataBase = PromiseDataBase(this@SearchActivity)
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : PromiseAdapter
    lateinit var materialSearchBar : MaterialSearchBar
    var choiceOfSort : Sort = Sort.NAME
    var valeurActuelle : String = ""
    private lateinit var deleteButton : FloatingActionButton
    private lateinit var deleteListener : DeleteButtonListener
    private lateinit var slidr: SlidrInterface
    var  config : SlidrConfig =  SlidrConfig.Builder()
        .position(SlidrPosition.LEFT)
        .sensitivity(1f)
        .scrimColor(Color.BLACK)
        .scrimStartAlpha(0.8f)
        .scrimEndAlpha(0f)
        .velocityThreshold(2400F)
        .distanceThreshold(0.25f)
        .edge(true)
        .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
        .build()

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        slidr = Slidr.attach(this, utils.config);
        setContentView(R.layout.activity_search)
        recyclerView  = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        materialSearchBar = findViewById(R.id.searchBar)
        deleteButton = findViewById(R.id.deleteButton)
        materialSearchBar.inflateMenu(R.menu.app_menu)
        materialSearchBar.menu.setOnMenuItemClickListener(this as PopupMenu.OnMenuItemClickListener)
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),user.name))
       //user.loadPromises(db = promiseDataBase)
        listPromesses = user.getAllPromise()
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customSuggestionAdapter = CustomSuggestionAdapter(layoutInflater, this)
        customSuggestionAdapter.suggestions = listPromesses.toMutableList()
        materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //materialSearchBar.clearSuggestions()
                //materialSearchBar.hideSuggestionsList()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (start > 0) {
                    var suggest =
                        user.getSearchResultsSorted((materialSearchBar.text.toLowerCase()),
                            choiceOfSort).toMutableList()

                    if (suggest.size < 1) {
                        //materialSearchBar.clearSuggestions()
                        materialSearchBar.hideSuggestionsList()
                    } else {
                        materialSearchBar.updateLastSuggestions(suggest)
                    }
                }
            }
        })

        materialSearchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            /**
             * On search state changed
             *
             * @param enabled
             */
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    recyclerView.adapter = adapter
                }
                //materialSearchBar.clearSuggestions()
            }

            /**
             * On search confirmed
             *
             * @param text
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
     * Lock slider
     *
     */
    private fun lockSlider(){
        slidr.lock()
    }

    /**
     * Un lock slider
     *
     */
    private fun unLockSlider(){
        slidr.unlock()
    }

    /**
     * Start research
     *
     * @param text
     * @param relaunch
     */
    private fun startResearch(text: String, relaunch : Boolean = true) {
        if (relaunch)
        {
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
        listPromesses  = user.getAllPromise()
        customSuggestionAdapter.suggestions = listPromesses.toMutableList()
        materialSearchBar.closeSearch()
    }

    /**
     * Hide keyboard
     *
     * @param activity
     */
    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * On menu item click
     *
     * @param item
     * @return
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menudate -> choiceOfSort = Sort.DATE
            R.id.menupriority -> choiceOfSort = Sort.PRIORITY
            R.id.menuname -> choiceOfSort = Sort.NAME
        }
        startResearch(valeurActuelle, false)
        return true
    }

    /**
     * On resume
     *
     */
    override fun onResume() {
        super.onResume()
        listPromesses = user.getAllPromise()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this),this)
        deleteListener = DeleteButtonListener(adapter, this)
        deleteButton.setOnClickListener(deleteListener)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),user.name))
    }

    /**
     * On add button clicked
     *
     * @param v
     */
    fun onAddButtonClicked (v : View) {
        val intent = Intent(this, PromiseManagerActivity::class.java)
        startActivity(intent)
    }

    /**
     * On item click
     *
     * @param v
     */
    override fun onItemClick(v: View?) {
        var text = (v as TextView).text.toString()
        startResearch(text)
    }

}


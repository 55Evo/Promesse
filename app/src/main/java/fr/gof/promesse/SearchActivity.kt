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


class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, CustomSuggestionAdapter.OnItemClickListener {

    lateinit var customSuggestionAdapter: CustomSuggestionAdapter
    lateinit var listPromesses: MutableList<Promise>
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
        .build();
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
       // materialSearchBar.setCardViewElevation(10)
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),user.name))





        listPromesses = user.getAllPromise().toMutableList()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this),this)
        deleteListener = DeleteButtonListener(adapter, this, promiseDataBase)
        deleteButton.setOnClickListener(deleteListener)
        //deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        //adapter.notifyDataSetChanged()


        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customSuggestionAdapter = CustomSuggestionAdapter(layoutInflater, this)

        customSuggestionAdapter.suggestions = listPromesses
        materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)



//
        customSuggestionAdapter.suggestions = user.getAllPromise().toList()
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

                        Log.d("_______________________---------------_________________",
                            suggest.size.toString())
                        Log.d("_______________________----- ok ----------_________________",
                            suggest.size.toString())
                        //materialSearchBar.clearSuggestions()
                        materialSearchBar.hideSuggestionsList()
                    } else {
                        //materialSearchBar.lastSuggestions = suggest

                        //materialSearchBar.lastSuggestions = user.getSearchResultsSorted(materialSearchBar.text.toLowerCase(), Sort.DATE).toList()

                       // customSuggestionAdapter.suggestions = suggest
                        materialSearchBar.updateLastSuggestions(suggest)
                    }
                }
              //  }
               // materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
//                var suggest = mutableListOf<Promise>()
//               // materialSearchBar.setMaxSuggestionCount(0);
//               // materialSearchBar.updateLastSuggestions(suggest)
//
//                for (search in customSuggestionAdapter.suggestions) {
//                    if (search.title.toLowerCase().contains(materialSearchBar.text.toLowerCase())) {
//                        suggest.add(search)
//                    }
//                    //customSuggestionAdapter.suggestions = suggest
//                    materialSearchBar.lastSuggestions = suggest
//
//                        customSuggestionAdapter.suggestions = suggest
//
//                }
//                materialSearchBar.setCustomSuggestionAdapter(customSuggestionAdapter)
//                // ?????????????? !!!!!!!!!!!!!!!!!!!!!
//               materialSearchBar.setMaxSuggestionCount(suggest.size);
//                materialSearchBar.updateLastSuggestions(suggest)



//                recyclerView.adapter = customSuggestionAdapter
//                customSuggestionAdapter.notifyDataSetChanged()



            }
        })

        materialSearchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    recyclerView.adapter = adapter
                }
                //materialSearchBar.clearSuggestions()
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startResearch(text.toString())
            }
            override fun onButtonClicked(buttonCode: Int) {
//                adapter.notifyDataSetChanged()
//
//                materialSearchBar.closeSearch()
//                materialSearchBar.clearSuggestions()
//                materialSearchBar.hideSuggestionsList()


            }
        })

       // listPromesses = utils.user.getSearchResultsSorted("", choiceOfSort, promiseDataBase).toMutableList()
        listPromesses = user.getAllPromise().toMutableList()
        this.recyclerView.adapter = adapter
    }
    private fun lockSlider(){
        slidr.lock()
    }
    private fun unLockSlider(){
        slidr.unlock()
    }
    private fun startResearch(text: String, relaunch : Boolean = true) {
        if (relaunch)
        {
            materialSearchBar.closeSearch()
            materialSearchBar.setPlaceHolder(text)


        }

        valeurActuelle = text
        listPromesses = user.getSearchResultsSorted(text, choiceOfSort).toMutableList()
        deleteButton.visibility = View.INVISIBLE

        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this), this)
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        materialSearchBar.clearSuggestions()
        materialSearchBar.hideSuggestionsList()
        hideKeyboard(this)
        materialSearchBar.closeSearch()

    }

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
        listPromesses = user.getAllPromise().toMutableList()
        adapter = PromiseAdapter(listPromesses, PromiseEventListener(listPromesses, this),this)
        deleteListener.adapter = adapter
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        materialSearchBar.setPlaceHolder(String.format(getString(R.string.searchbarPlaceholder),user.name))
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


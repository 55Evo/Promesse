package fr.gof.promesse.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import fr.gof.promesse.R
import fr.gof.promesse.model.Promise


/**
 * Custom suggestion adapter
 *
 * @property listener
 * @constructor
 *
 * @param inflater
 *
 * Adapter des suggestion de promesses dans la classe Research
 */
class CustomSuggestionAdapter(inflater: LayoutInflater, val listener : CustomSuggestionAdapter.OnItemClickListener, val context : Context) : SuggestionsAdapter<Promise, CustomSuggestionAdapter.SuggestionHolder>(inflater) {

    /**
     * On bind suggestion holder
     *
     * @param promise
     * @param holder
     * @param position
     *
     * Permet de mettre à jour la vue de la suggestion comprenant le titre de la promesse
     */
    override fun onBindSuggestionHolder(promise: Promise, holder: SuggestionHolder, position: Int) {
        holder.title.text = promise.title
        holder.title.setTextColor(context.getColor(R.color.black))

    }

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     *
     * Création de l'adapter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val view: View = layoutInflater.inflate(R.layout.item_suggest, parent, false)
        return SuggestionHolder(view)
    }

    override fun getSingleViewHeight(): Int {
        return 40
    }

    /**
     * Suggestion holder
     *
     * @constructor
     *
     * @param itemView
     * Classe interne permettant d'interragir avec les éléments de la vue
     */
    inner class SuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var title: TextView
        init {
            title = itemView.findViewById(R.id.textSuggest)
            title.setOnClickListener(this)
        }

        /**
         * On click
         *
         * @param v
         */
        override fun onClick(v: View?) {
            listener.onItemClick(v)
        }
    }


    /**
     * On item click listener
     *
     * @constructor Create empty On item click listener
     */
    interface OnItemClickListener {
        /**
         * On item click
         *
         * @param v
         */
        fun onItemClick(v : View?)
    }


}


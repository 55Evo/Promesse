package fr.gof.promesse.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise


class CustomSuggestionAdapter(inflater: LayoutInflater, val listener : CustomSuggestionAdapter.OnItemClickListener) : SuggestionsAdapter<Promise, CustomSuggestionAdapter.SuggestionHolder>(inflater) {

    override fun onBindSuggestionHolder(promise: Promise, holder: SuggestionHolder, position: Int) {
        holder.title.text = promise.title;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val view: View = layoutInflater.inflate(R.layout.suggest_item, parent, false)

        return SuggestionHolder(view)
    }

    override fun getSingleViewHeight(): Int {
        return 40
    }

    inner class SuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var title: TextView
        init {
            title = itemView.findViewById(R.id.textSuggest)
            title.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v)
        }
    }


    interface OnItemClickListener {
        fun onItemClick(v : View?)
    }


}


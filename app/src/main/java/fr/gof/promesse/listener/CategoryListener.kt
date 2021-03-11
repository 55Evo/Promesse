package fr.gof.promesse.listener

import android.app.Activity
import fr.gof.promesse.adapter.CategoryAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Category


/**
 * Mascot listener
 *
 * @property listCategory
 * @property context
 * @constructor Create empty Mascot listener
 */
class CategoryListener(var listCategory: List<Category>, var context: Activity): CategoryAdapter.OnItemClickListener {


    override fun onItemClick(position: Int, adapter: CategoryAdapter, database: PromiseDataBase) {
        var category : Category = listCategory[position]
        adapter.chosenCategory = category
       // database.updateCategory(category, promise)
    }

}

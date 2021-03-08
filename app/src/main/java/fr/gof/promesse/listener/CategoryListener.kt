package fr.gof.promesse.listener

import android.app.Activity
import android.content.Intent
import fr.gof.promesse.adapter.MascotAdapter
import fr.gof.promesse.MainActivity
import fr.gof.promesse.adapter.CategoryAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Category
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.Promise


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
        adapter.chooenCategory = category
       // database.updateCategory(category, promise)
    }

}

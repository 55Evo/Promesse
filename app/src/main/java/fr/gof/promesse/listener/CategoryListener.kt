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
 *
 * Listener des carégorie permettant d'override le onClick lorsque l'on clic sur une catégorie
 * dans la classe PromiseManagerActivity et mettre à jour l'adapter
 */
class CategoryListener(var listCategory: List<Category>, var context: Activity) :
    CategoryAdapter.OnItemClickListener {
    override fun onItemClick(position: Int, adapter: CategoryAdapter, database: PromiseDataBase) {
        var category: Category = listCategory[position]
        adapter.chosenCategory = category
    }

}

package fr.gof.promesse.model

import fr.gof.promesse.R

/**
 * Mascot Enum
 *
 * @property nom
 * @property image
 * @property image_drawable
 */
enum class Category (val nom : String, val background :Int, val image_drawable :Int) {
    SPORT("Sport", R.drawable.sport, R.drawable.sport),
    CUISINE("Cuisine", R.drawable.cuisine, R.drawable.cuisine),
    ETUDES ("Etudes", R.drawable.etudes, R.drawable.etudes),
    LOISIRS ("Loisirs", R.drawable.loisirs, R.drawable.loisirs),
    DEFAUT ("Defaut", R.drawable.defaut_background, R.drawable.defaut),
}
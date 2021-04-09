package fr.gof.promesse.model

import fr.gof.promesse.R

/**
 * Mascot Enum
 *
 * @property nom
 * @property image
 * @property image_drawable
 */
enum class Category(
    val nom: String,
    var background: Int,
    var image_drawable: Int,
    var check: Boolean = false
) {
    SPORT("Sport", R.drawable.sport_background, R.drawable.sport),
    CUISINE("Cuisine", R.drawable.manger, R.drawable.logo_manger),
    ETUDES("Etudes", R.drawable.travail_background, R.drawable.etudes),
    LOISIRS("Loisirs", R.drawable.loisirs_background, R.drawable.loisirs),
    DEFAUT("Defaut", R.drawable.defaut_background, R.drawable.defaut),
}
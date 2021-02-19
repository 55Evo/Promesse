package fr.gof.promesse.model

import fr.gof.promesse.R

enum class Mascot (val nom : String, val image :Int, val image_drawable :Int) {
    JACOU("Jacou le Hibou", R.drawable.mascot1, R.drawable.mascot_afficher_1),
    RAYMOND("Raymond Le Crayon", R.drawable.mascot2, R.drawable.mascot_afficher_2),
    EUSTACHE("Eustache la Vache", R.drawable.mascot3, R.drawable.mascot_afficher_3)
}

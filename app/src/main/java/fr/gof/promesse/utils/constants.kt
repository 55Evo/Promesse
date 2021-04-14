package utils

import android.graphics.Color
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User


const val NOTIFICATION_CHANNEL_ID = "100"

/**
 * LEFT / RIGHT are constants to know if user swipe to left or right
 *
 * Ce sont des constantes pour savoir si l'utilisateur swipe à gauche ou à droite.
 */
const val LEFT = 4
const val RIGHT = 8
var config: SlidrConfig = SlidrConfig.Builder()
    .position(SlidrPosition.TOP)
    .sensitivity(1f)
    .scrimColor(Color.BLACK)
    .scrimStartAlpha(0.9f)
    .scrimEndAlpha(0f)
    .velocityThreshold(1000F)
    .distanceThreshold(0.5f)
    .edge(true)
    .edgeSize(0.15f) // The % of the screen that counts as the edge, default 18%
    .build()

/**
 * Current width start / current width end are the size of promise
 * at the start and the end of the animation of deployment.
 *
 * Utilse lors du déploiement d'une promesse, on veut connaître la taille
 * de départ et la taille d'arrivée.
 */
const val currentWidthStart = 150
const val currentWidthEnd = 250

/**
 * End size / start size are the font size of promise title at the start
 * and the end of the animation of deployment.
 *
 * Taille du titre des promesses au début et à la fin de l'animation
 * de déploiement.
 */
const val endSize = 20f
const val startSize = 16f
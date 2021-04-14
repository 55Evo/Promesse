package fr.gof.promesse.model

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import fr.gof.promesse.R
import java.util.*

/**
 * Mascot Enum
 *
 * @property nom
 * @property image
 * @property image_drawable
 */
enum class Mascot(val nom: String, val image: Int, val image_drawable: Int, private val shout: String) {
    JACOU("Jacou le Hibou", R.drawable.mascot1, R.drawable.mascot_afficher_1, "Houhou !"),
    RAYMOND("Raymond Le Crayon", R.drawable.mascot2, R.drawable.mascot_afficher_2, "Cricrr !"),
    EUSTACHE("Eustache la Vache", R.drawable.mascot3, R.drawable.mascot_afficher_3, "Meeuuh !");

    /**
     * Mascot welcome message that sends a message when user launch the application.
     *
     * @param context
     * @param listPromesse
     * @param view
     *
     * Méthode qui envoie un message de la mascotte à l'utilisateur quand il ouvre l'application.
     */
    fun mascotWelcomeMessage(context: Context, listPromesse: TreeSet<Promise>, view: View) {
        val now = Calendar.getInstance()
        if (now.get(Calendar.HOUR_OF_DAY) in 6..20) {
            displayMascotMessage(context.getString(R.string.dayMascotMessage), view, context)
        } else {
            displayMascotMessage(context.getString(R.string.nightMascotMessage), view, context)
        }
        if (listPromesse.size > 3) {
            displayMascotMessage(
                String.format(
                    context.getString(R.string.welcomeMascotMessageMassPromises),
                    listPromesse.size
                ) + shout, view, context, 1
            )
        } else {
            if (listPromesse.size <= 0) {
                displayMascotMessage(
                    context.getString(R.string.welcomeMascotMessageNoPromise) + shout,
                    view,
                    context,
                    1
                )
            } else {
                displayMascotMessage(
                    String.format(
                        context.getString(R.string.welcomeMascotMessageLowPromises),
                        listPromesse.size
                    ) + shout, view, context, 1
                )
            }
        }
    }

    /**
     * Display mascot message that displays messages of the mascot.
     *
     * @param message to display
     *
     * Affiche les messages de la mascotte.
     */
    fun displayMascotMessage(message: String, view: View, context: Context, order: Long = 0) {
        Handler().postDelayed({
            val bubble: TextView = view as TextView
            bubble.text = message
            bubble.visibility = View.VISIBLE
            bubble.animation = AnimationUtils.loadAnimation(
                context,
                R.anim.displaybubble
            )
            bubble.animate()
            Handler().postDelayed({
                bubble.visibility = View.GONE
            }, 5000)
        }, 5500 * order)
    }

}
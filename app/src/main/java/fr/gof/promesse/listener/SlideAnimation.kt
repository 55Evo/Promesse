package fr.gof.promesse.listener

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Slide animation
 *
 * @property mview
 * Classe permettant le slide d'une promesse de haut en bas lors du clic (déploiement)
 */
class SlideAnimation(var mview: View) : Animation() {
    /**
     * Expand
     *
     * @param choice
     * Fonction permettant d'agrandir ou de rétrécir la taille de la bulle contant la promesse
     * en faisant une animation en appelant la bonne fonction
     */
    fun expand(choice: Boolean) {
        val animation: Animation = if (choice) {
            expandAction(mview)
        } else {
            collapseView(mview)
        }
        animation.duration = 300
        animation.interpolator = AccelerateInterpolator()
        mview.startAnimation(animation)

    }

    /**
     * Expand action
     *
     * @param view
     * @return
     * Fonction permettant le déploiement d'une promesse
     */
    private fun expandAction(view: View): Animation {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val actualHeight = view.measuredHeight
        view.layoutParams.height = 0
        //view.visibility = View.VISIBLE
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualHeight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
        return animation
    }

    /**
     * Collapse view
     *
     * @param view
     * @return
     * Fonction permettant le rétrécissement d'une promesse
     */
    fun collapseView(view: View): Animation {
        val actualHeight = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        actualHeight - (actualHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }
        animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
        return animation
    }

}
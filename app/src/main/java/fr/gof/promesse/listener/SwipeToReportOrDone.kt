package fr.gof.promesse.listener

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R

/**
 * Swipe to report or done
 *
 * @property mContext
 * Classe permettant de gérer les interraction de swipe ainsi que de drag and drop avec une promesse
 */
abstract class SwipeToReportOrDone internal constructor(private var mContext: Context) :
    ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColorReport: String = "DE8282"
    private val backgroundColorDone: String = "A1C26C"
    private val backgroundColorMoove: String = "#F22B00"
    private var reportDrawable: Drawable
    private var doneDrawable: Drawable
    private val iconWidthDone: Int
    private val iconHeightDone: Int
    private val space = 50
    private val iconWidthReport: Int
    private val iconHeightReport: Int

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        reportDrawable =
            ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_report_24) as Drawable
        doneDrawable =
            ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_done_24) as Drawable
        iconWidthReport = reportDrawable.intrinsicWidth
        iconHeightReport = reportDrawable.intrinsicHeight
        iconWidthDone = doneDrawable.intrinsicWidth
        iconHeightDone = doneDrawable.intrinsicHeight
        doneDrawable.alpha = 0
        reportDrawable.alpha = 0

    }

    /**
     * Get movement flags
     *
     * @param recyclerView
     * @param viewHolder
     *return int numéro de geste
     * Permet de dire que l'on autorise le swipe vers la gauche + le swipe vers la droite
     * et que l'on autorise le drag and drop vers le haut et vers le bas
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * Is long press drag enabled
     *
     * @return boolean
     * Fonction qui active le drag and drop en faisant un appuie renforcé sur la promesse
     */
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }


    /**
     * On child draw
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     *
     * Si l'on déplace la promesse vers la gauche on lui applique un certain traitement et idem pour
     * la droite. Pour le drag and drop on applique un autre traitement
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        when {
            dX < 0 -> { // left case
                leftTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            dX > 0 -> { //right treatment
                rightTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            dY != (0F) -> {
                onMooveTreatment(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    }

    /**
     * On moove treatment
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     *
     * Quand on drag and drop une promesse cette fonction permet d'afficher un rectangle rouge à sa
     * gauche permettant visuellement de voir quelle est sélectionnée sinon de l'enlever
     */
    private fun onMooveTreatment(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        itemView.height
        val isCancelled = dY == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackgroundMoove(itemView, c, dY) // affiche ce rectangle rouge
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Set background moove
     *
     * @param itemView
     * @param c
     * @param dY
     * Affiche le rectangle rouge permettant de voir la promesse sélectionnée dans le drag and drop
     */
    private fun setBackgroundMoove(itemView: View, c: Canvas, dY: Float) {
        mBackground.color = Color.parseColor(backgroundColorMoove)

        mBackground.setBounds(
            itemView.left - space,
            itemView.top + 30 + dY.toInt(),
            itemView.left - 3,
            itemView.bottom - 30 + dY.toInt()
        )
        mBackground.alpha = 200
        mBackground.draw(c)
    }

    /**
     * Left treatment
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     * Affiche et supprime le logo done (logo qui s'affiche quand on swipe la promesse vers la gauche)
     */
    private fun leftTreatment(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.left + dX,
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackgroundDone(itemView, dX, c, itemHeight)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Right treatment
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     * Affiche et supprime le logo repport (logo qui s'affiche quand on swipe la promesse vers la gauche)
     */
    private fun rightTreatment(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackgroundReport(itemView, dX, c, itemHeight)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Set background done
     *
     * @param itemView
     * @param dX
     * @param c
     * @param itemHeight
     * Affiche le logo done sur la background quand on swipe vers la gauche en faisant apparaitre
     * petit à petit l'icone ainsi que la couleur de fond
     */
    private fun setBackgroundDone(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        giveColor(backgroundColorDone, false, dX.toInt())

        mBackground.setBounds(
            itemView.right - 50 + dX.toInt(),
            itemView.top + 4,
            itemView.right + 15,
            itemView.bottom - 4
        )
        mBackground.draw(c)
        val doneIconTop = itemView.top + (itemHeight - iconHeightDone) / 2
        val doneIconMargin = (itemHeight - iconHeightDone) / 4
        val doneIconLeft = itemView.right - doneIconMargin - iconWidthDone
        val doneIconRight = itemView.right - doneIconMargin
        val deleteIconBottom = doneIconTop + iconHeightDone
        doneDrawable.setBounds(doneIconLeft, doneIconTop, doneIconRight, deleteIconBottom)
        doneDrawable.draw(c)
    }

    /**
     * Give color
     *
     * @param colorString
     * @param isRight
     * @param dX
     * Fonction qui attribue une couleur en fonction du swipe effectué plus ou moins transparente
     */
    private fun giveColor(colorString: String, isRight: Boolean, dX: Int) {
        var minus: Int = -1000
        if (isRight) minus = 1000
        var calcul = Integer.toHexString(dX * 255 / (minus))
        if (calcul.length < 2) {
            calcul = "0$calcul"
        }

        if (dX <= -1000 || (dX >= 1000)) {
            calcul = "FF"
        }
        if ((dX < 1000) || (dX >= 1000)) {
            doneDrawable.alpha = kotlin.math.abs(dX / 4)
            reportDrawable.alpha = kotlin.math.abs(dX / 4)
        } else if (calcul == "FF") {
            doneDrawable.alpha = 255
            reportDrawable.alpha = 255
        }
        mBackground.color = Color.parseColor("#$calcul$colorString")

    }

    /**
     * Set background report
     *
     * @param itemView
     * @param dX
     * @param c
     * @param itemHeight
     * Affiche le logo repport sur la background quand on swipe vers la droite en faisant apparaitre
     * petit à petit l'icone ainsi que la couleur de fond
     */
    private fun setBackgroundReport(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        giveColor(backgroundColorReport, true, dX.toInt())

        mBackground.setBounds(
            itemView.left + dX.toInt() + 50,
            itemView.top + 4,
            itemView.left - 15,
            itemView.bottom - 4
        )
        mBackground.draw(c)
        val reportIconTop = itemView.top + (itemHeight - iconHeightReport) / 2
        val reportIconMargin = (itemHeight - iconHeightReport) / 4
        val reportIconLeft = itemView.left + reportIconMargin
        val reportIconRight = itemView.left + reportIconMargin + iconWidthReport
        val reportIconBottom = reportIconTop + iconHeightReport
        reportDrawable.setBounds(reportIconLeft, reportIconTop, reportIconRight, reportIconBottom)
        reportDrawable.draw(c)
    }

    /**
     * Clear canvas
     *
     * @param c
     * @param left
     * @param top
     * @param right
     * @param bottom
     * Efface les éléments affichés (rectangle rouge pour le drag and drop, logos repport/done...)
     */
    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    /**
     * Get swipe threshold
     *
     * @param viewHolder
     * @return renvoie la fraction que l'utilisateur doit déplacer la vue pour qu'elle soit considérée comme glissée.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }


}
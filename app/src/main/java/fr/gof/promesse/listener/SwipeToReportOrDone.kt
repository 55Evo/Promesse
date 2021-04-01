
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import java.lang.Math.abs
import kotlin.math.roundToInt

abstract class SwipeToReportOrDone internal constructor(var mContext: Context) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColorReport: String = "DE8282"
    private val backgroundColorDone: String = "A1C26C"
    private val backgroundColorMoove: String = "#F22B00"
    private var reportDrawable: Drawable
    private var doneDrawable : Drawable
    private val iconWidthDone: Int
    private val iconHeightDone: Int
    private val space = 50

    private val iconWidthReport : Int
    private val iconHeightReport : Int


    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        reportDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_report_24) as Drawable
        doneDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_done_24) as Drawable
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
     * @return
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * Is long press drag enabled
     *
     * @return
     */
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    /**
     * On move
     *
     * @param recyclerView
     * @param viewHolder
     * @param viewHolder1
     * @return
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
            return true;
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
     */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        when {
            dX<0 -> { // left case
                leftTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            dX>0 -> { //right treatment
                rightTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            dY!=(0F)-> {
                onMooveTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
     */
    private fun onMooveTreatment(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean){
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dY == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackgroundMoove(itemView, c,dY)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Set background moove
     *
     * @param itemView
     * @param c
     * @param dY
     */
    private fun setBackgroundMoove(itemView: View, c: Canvas, dY: Float){
        mBackground.color = Color.parseColor(backgroundColorMoove)

        mBackground.setBounds(itemView.left -space, itemView.top+30 + dY.toInt(), itemView.left-3, itemView.bottom-30 + dY.toInt())
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
     */
    private fun leftTreatment(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
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
     */
    private fun rightTreatment(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
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
     */
    private fun setBackgroundDone(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        giveColor (backgroundColorDone, false, dX.toInt())

        mBackground.setBounds(itemView.right - 50 + dX.toInt(), itemView.top + 4, itemView.right+15, itemView.bottom - 4)
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
     */
    private fun giveColor (colorString : String, isRight : Boolean, dX : Int){
        var minus : Int = -1000
        if (isRight) minus = 1000
        var calcul = Integer.toHexString(dX *255/ (minus))
        if (calcul.length < 2){
            calcul = "0$calcul"
        }

            if (dX<=-1000 || (dX >=1000)){
                calcul = "FF"
            }
        if( (dX < 1000) || (dX >=1000)){
            doneDrawable.alpha = kotlin.math.abs(dX / 4)
            reportDrawable.alpha = kotlin.math.abs(dX / 4)
        }
        else if(calcul == "FF"){
            doneDrawable.alpha =  255
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
     */
    private fun setBackgroundReport(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        giveColor (backgroundColorReport, true, dX.toInt())

        mBackground.setBounds(itemView.left + dX.toInt() + 50, itemView.top + 4, itemView.left-15, itemView.bottom - 4)
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
     */
    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    /**
     * Get swipe threshold
     *
     * @param viewHolder
     * @return Returns the fraction that the user should move the View to be considered as swiped.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
    




}
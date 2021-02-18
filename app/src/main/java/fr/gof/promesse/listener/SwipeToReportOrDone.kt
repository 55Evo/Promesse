
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R

abstract class SwipeToReportOrDone internal constructor(var mContext: Context) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColorReport: Int = ContextCompat.getColor(mContext, R.color.light_red)
    private val backgroundColorDone: Int = ContextCompat.getColor(mContext,R.color.light_green)
    private var reportDrawable: Drawable
    private var doneDrawable : Drawable

    private val iconWidthDone: Int
    private val iconHeightDone: Int

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

    }


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
        return false
    }
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (dX<0){ // left case
                leftTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
       else{ //right treatment
                rightTreatment(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

    }

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

    private fun rightTreatment(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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

    private fun setBackgroundDone(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        mBackground.color = backgroundColorDone
        mBackground.setBounds(itemView.right - 50 + dX.toInt(), itemView.top + 15, itemView.right, itemView.bottom - 15)
        mBackground.draw(c)
        val doneIconTop = itemView.top + (itemHeight - iconHeightDone) / 2
        val doneIconMargin = (itemHeight - iconHeightDone) / 4
        val doneIconLeft = itemView.right - doneIconMargin - iconWidthDone
        val doneIconRight = itemView.right - doneIconMargin
        val deleteIconBottom = doneIconTop + iconHeightDone
        doneDrawable.setBounds(doneIconLeft, doneIconTop, doneIconRight, deleteIconBottom)
        doneDrawable.draw(c)
    }

    private fun setBackgroundReport(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        mBackground.color = backgroundColorReport
        mBackground.setBounds(itemView.left + dX.toInt() + 50, itemView.top + 15, itemView.left, itemView.bottom - 15)
        mBackground.draw(c)
        val reportIconTop = itemView.top + (itemHeight - iconHeightReport) / 2
        val reportIconMargin = (itemHeight - iconHeightReport) / 4
        val reportIconLeft = itemView.left + reportIconMargin
        val reportIconRight = itemView.left + reportIconMargin + iconWidthReport
        val reportIconBottom = reportIconTop + iconHeightReport
        reportDrawable.setBounds(reportIconLeft, reportIconTop, reportIconRight, reportIconBottom)
        reportDrawable.draw(c)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    // Returns the fraction that the user should move the View to be considered as swiped.
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }


}
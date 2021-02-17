
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R

abstract class SwipeToReport internal constructor(var mContext: Context) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor: Int = Color.parseColor("#DCA599")
    private var reportDrawable: Drawable
    private val iconWidth: Int
    private val iconHeight: Int
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
        return false
    }
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackground(itemView, dX, c, itemHeight)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun setBackground(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        mBackground.color = backgroundColor
        mBackground.setBounds(itemView.left + dX.toInt() + 50, itemView.top + 15, itemView.left, itemView.bottom - 15)
        mBackground.draw(c)
        val reportIconTop = itemView.top + (itemHeight - iconHeight) / 2
        val reportIconMargin = (itemHeight - iconHeight) / 3
        val reportIconLeft = itemView.left + reportIconMargin
        val reportIconRight = itemView.left + reportIconMargin + iconWidth
        val reportIconBottom = reportIconTop + iconHeight
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

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        reportDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_report_24) as Drawable
        iconWidth = reportDrawable.intrinsicWidth
        iconHeight = reportDrawable.intrinsicHeight
    }
}

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R

abstract class SwipeToDone internal constructor(var mContext: Context) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor: Int = Color.parseColor("#ACEC48")
    private var doneDrawable: Drawable
    private val iconWidth: Int
    private val iconHeight: Int
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
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
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        setBackground(itemView, dX, c, itemHeight)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun setBackground(itemView: View, dX: Float, c: Canvas, itemHeight: Int) {
        mBackground.color = backgroundColor
        mBackground.setBounds(itemView.right - 50 + dX.toInt(), itemView.top + 15, itemView.right, itemView.bottom - 15)
        mBackground.draw(c)
        val doneIconTop = itemView.top + (itemHeight - iconHeight) / 2
        val doneIconMargin = (itemHeight - iconHeight) / 2
        val doneIconLeft = itemView.right - doneIconMargin - iconWidth
        val doneIconRight = itemView.right - doneIconMargin
        val deleteIconBottom = doneIconTop + iconHeight
        doneDrawable.setBounds(doneIconLeft, doneIconTop, doneIconRight, deleteIconBottom)
        doneDrawable.draw(c)
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
        doneDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_done_24) as Drawable
        iconWidth = doneDrawable.intrinsicWidth
        iconHeight = doneDrawable.intrinsicHeight
    }
}

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R

abstract class SwipeupDown internal constructor(var mContext: Context) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColorReport: Int = ContextCompat.getColor(mContext, R.color.light_red)
    private val backgroundColorDone: Int = ContextCompat.getColor(mContext, R.color.light_green)
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
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN //ItemTouchHelper.UP or ItemTouchHelper.DOWN //or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
            return true;
    }



    // Returns the fraction that the user should move the View to be considered as swiped.

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return super.getSwipeVelocityThreshold(defaultValue)
    }


}
package y2k.joyreactor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.forEachChild
import y2k.joyreactor.common.measureSpec
import kotlin.properties.Delegates

/**
 * Created by y2k on 9/26/15.
 */
class FixedAspectPanel(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    var aspect by Delegates.observable(1f) { prop, old, new ->
        if (old != new) requestLayout()
    }

    init {
        if (isInEditMode) aspect = 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w: Int
        val h: Int
        if (View.MeasureSpec.getSize(widthMeasureSpec) != 0) {
            w = View.MeasureSpec.getSize(widthMeasureSpec)
            h = (w / aspect).toInt()
        } else if (View.MeasureSpec.getSize(heightMeasureSpec) != 0) {
            h = View.MeasureSpec.getSize(heightMeasureSpec)
            w = (h * aspect).toInt()
        } else {
            throw IllegalStateException()
        }
        setMeasuredDimension(w, h)

        forEachChild {
            it.measure(
                w.measureSpec(View.MeasureSpec.EXACTLY),
                h.measureSpec(View.MeasureSpec.EXACTLY))
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEachChild { it.layout(0, 0, r - l, b - t) }
    }
}
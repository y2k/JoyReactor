package y2k.joyreactor.widget

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.R
import y2k.joyreactor.common.dipToPx
import y2k.joyreactor.common.getChildren
import y2k.joyreactor.common.inflate
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by y2k on 3/2/16.
 */
class TagsView(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    val divider = 8.dipToPx()
    val positions = ArrayList<Point>()

    var tags by Delegates.observable(emptyList<String>()) { prop, old, new ->
        populate()
    }

    init {
        layoutTransition = LayoutTransition()
    }

    private fun populate() {
        removeAllViews()
        tags.forEach {
            addView((inflate(R.layout.item_post_tag) as TextView).apply {
                text = "#${it.toLowerCase()}"
                setPadding(8.dipToPx(), 8.dipToPx(), 8.dipToPx(), 8.dipToPx())
                setBackgroundResource(R.drawable.drawable_tag)
            })
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val post = Point()
        var height = 0
        positions.clear()

        for (child in getChildren()) {
            child.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), MeasureSpec.UNSPECIFIED)

            if (post.x + child.measuredWidth > maxWidth) {
                height += post.y + divider
                post.x = 0
                post.y = 0
            }
            positions.add(Point(paddingLeft + post.x, paddingTop + height))
            post.x += child.measuredWidth + divider
            post.y = Math.max(post.y, child.measuredHeight)
        }

        height += post.y
        setMeasuredDimension(maxWidth + paddingLeft + paddingRight, height + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0..childCount - 1) {
            val c = getChildAt(i)
            val p = positions[i]
            c.layout(p.x, p.y, p.x + c.measuredWidth, p.y + c.measuredHeight)
        }
    }
}
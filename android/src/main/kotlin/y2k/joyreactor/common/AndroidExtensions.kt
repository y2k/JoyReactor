package y2k.joyreactor.common

import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.App

/**
 * Created by y2k on 2/1/16.
 */

fun Int.dipToPx(): Int {
    return (this * App.instance.resources.displayMetrics.density).toInt()
}

fun Int.measureSpec(spec: Int): Int {
    return View.MeasureSpec.makeMeasureSpec(this, spec)
}

fun ViewGroup.forEachChild(func: (View) -> Unit) {
    for (i in 0..childCount - 1)
        func(getChildAt(i))
}
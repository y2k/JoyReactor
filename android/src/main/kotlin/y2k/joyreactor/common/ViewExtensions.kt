package y2k.joyreactor.common

import android.app.Activity
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 2/6/16.
 */

fun View.compatAnimate(): ViewPropertyAnimatorCompat {
    return ViewCompat.animate(this)
}

var View.compatScaleY: Float
    get() = ViewCompat.getScaleY(this)
    set(value) = ViewCompat.setScaleY(this, value)

fun ViewGroup.switchByScaleFromTo(firstPosition: Int, secondPosition: Int) {
    val first = getChildAt(firstPosition)
    val second = getChildAt(secondPosition)
    first.compatAnimate()
        .scaleY(0f)
        .withEndAction {
            first.isVisible = false
            second.isVisible = true
            second.compatScaleY = 0f
            second.compatAnimate().scaleY(1f)
        }
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.find(id: Int): T {
    return findViewById(id) as T
}
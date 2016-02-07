package y2k.joyreactor.common

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.App
import kotlin.reflect.KClass

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

fun ViewGroup.inflateToSelf(layoutId: Int) {
    LayoutInflater.from(context).inflate(layoutId, this)
}

fun ViewGroup.inflate(layoutId: Int): View {
    return LayoutInflater.from(context).inflate(layoutId, this, false)
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun <T : Any> Activity.startActivity(type: KClass<T>) {
    startActivity(Intent(this, type.java))
}

@Suppress("UNCHECKED_CAST")
fun <T : View> Activity.find(id: Int): T {
    return findViewById(id) as T
}
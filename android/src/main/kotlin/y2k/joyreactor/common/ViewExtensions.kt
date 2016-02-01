package y2k.joyreactor.common

import y2k.joyreactor.App

/**
 * Created by y2k on 2/1/16.
 */

fun Int.dipToPx(): Int {
    return (this * App.instance.resources.displayMetrics.density).toInt()
}
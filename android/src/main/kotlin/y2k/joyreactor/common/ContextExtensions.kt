package y2k.joyreactor.common

import android.content.Context
import android.support.v4.content.ContextCompat

/**
 * Created by y2k on 5/29/16.
 */

fun Context.getColorCompat(colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}
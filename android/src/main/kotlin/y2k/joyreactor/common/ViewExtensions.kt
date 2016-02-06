package y2k.joyreactor.common

import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.view.View

/**
 * Created by y2k on 2/6/16.
 */

fun View.compatAnimate(): ViewPropertyAnimatorCompat {
    return ViewCompat.animate(this)
}
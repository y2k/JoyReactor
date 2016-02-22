package y2k.joyreactor.widget

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Created by y2k on 2/22/16.
 */
class AutoCloseDrawerPanel(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            (parent as DrawerLayout).closeDrawers()
        }
        return super.onInterceptTouchEvent(ev)
    }
}
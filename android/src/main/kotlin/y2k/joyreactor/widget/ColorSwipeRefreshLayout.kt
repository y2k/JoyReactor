package y2k.joyreactor.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import y2k.joyreactor.R

/**
 * Created by y2k on 9/26/15.
 */
class ColorSwipeRefreshLayout(context: Context, attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeResources(R.color.primary, R.color.primary_dark);
    }
}
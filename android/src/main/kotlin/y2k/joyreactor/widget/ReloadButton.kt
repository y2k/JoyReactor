package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import y2k.joyreactor.R
import y2k.joyreactor.common.inflateToSelf

/**
 * Created by y2k on 9/26/15.
 */
class ReloadButton(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        inflateToSelf(R.layout.layout_reload_button)
    }

    fun setVisibility(visibility: Boolean) {
        setVisibility(if (visibility) View.VISIBLE else View.GONE)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        findViewById(R.id.innerReloadButton).setOnClickListener(l)
    }
}
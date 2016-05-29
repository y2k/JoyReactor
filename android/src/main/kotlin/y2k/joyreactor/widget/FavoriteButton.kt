package y2k.joyreactor.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import y2k.joyreactor.R
import y2k.joyreactor.common.property

/**
 * Created by y2k on 5/29/16.
 */
class FavoriteButton(context: Context?, attrs: AttributeSet?) : AppCompatImageButton(context, attrs) {

    val isFavorite = property(false)

    init {
        setImageResource(R.drawable.ic_star)
        isFavorite.subscribe {
            setColorFilter(if (it) Color.YELLOW else Color.LTGRAY)
        }
    }
}
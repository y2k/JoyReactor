package y2k.joyreactor.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import y2k.joyreactor.R
import y2k.joyreactor.common.getColorCompat
import y2k.joyreactor.common.property

/**
 * Created by y2k on 5/29/16.
 */
class FavoriteButton(context: Context, attrs: AttributeSet?) : AppCompatImageButton(context, attrs) {

    val isFavorite = property(false)

    private val selectedColor = context.getColorCompat(R.color.primary)
    private val unselectedColor = Color.LTGRAY

    init {
        setImageResource(R.drawable.ic_stars_white_24dp)
        isFavorite.subscribe {
            setColorFilter(if (it) selectedColor else unselectedColor)
        }
    }
}
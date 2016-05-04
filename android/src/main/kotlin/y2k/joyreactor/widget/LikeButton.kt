package y2k.joyreactor.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import y2k.joyreactor.model.MyLike
import kotlin.properties.Delegates

/**
 * Created by y2k on 5/4/16.
 */
class LikeButton(context: Context?, attrs: AttributeSet?) : AppCompatImageButton(context, attrs) {

    var like by Delegates.observable(MyLike.Unknown) { p, old, new ->
        when (new) {
            MyLike.Like -> setColorFilter(Color.GREEN)
            MyLike.Dislike -> setColorFilter(Color.RED)
            else -> setColorFilter(Color.LTGRAY)
        }
    }
}
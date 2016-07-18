package y2k.joyreactor.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.widget.ImageView
import y2k.joyreactor.common.async.async_
import java.io.File

/**
 * Created by y2k on 12/8/15.
 */
@Deprecated("Use ProgressImageView")
class LargeImageView(context: Context, attrs: AttributeSet) :
    ImageView(context, attrs) {

    private var path: File? = null

    fun setImage(path: File) {
        if (this.path == path) return
        this.path = path

        async_ {
            val op = BitmapFactory.Options()
            op.inJustDecodeBounds = true
            runAsync { BitmapFactory.decodeFile(path.absolutePath, op) }

            val met = resources.displayMetrics
            op.inJustDecodeBounds = false
            op.inSampleSize = Math.max(
                op.outWidth / met.widthPixels,
                op.outHeight / met.heightPixels)

            val bitmap = runAsync { BitmapFactory.decodeFile(path.absolutePath, op) }
            setImageBitmap(bitmap)
        }
    }
}
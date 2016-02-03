package y2k.joyreactor

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.widget.ImageView
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.subscribeOnMain
import java.io.File

/**
 * Created by y2k on 12/8/15.
 */
class LargeImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    private var path: File? = null

    fun setImage(path: File) {
        if (this.path == path) return
        this.path = path

        ioObservable {
            val op = BitmapFactory.Options()
            op.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path.absolutePath, op)

            val met = resources.displayMetrics
            op.inJustDecodeBounds = false
            op.inSampleSize = Math.max(
                op.outWidth / met.widthPixels,
                op.outHeight / met.heightPixels)

            BitmapFactory.decodeFile(path.absolutePath, op)
        }.subscribeOnMain {
            setImageBitmap(it)
        }
    }
}
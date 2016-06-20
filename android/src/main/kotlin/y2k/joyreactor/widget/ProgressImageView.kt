package y2k.joyreactor.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.ui
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by y2k on 2/29/16.
 */
class ProgressImageView(context: Context?, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private val imageView = ImageView(context)
    private val progress = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)

    var image by Delegates.observable(PartialResult.inProgress<File>(0, 100)) { prop, old, new ->
        if (old == new) return@observable

        val file = new.result
        if (file == null) {
            progress.visibility = View.VISIBLE
            progress.progress = new.progress
            progress.max = new.max
        } else {
            progress.visibility = View.GONE
            ioObservable {
                val op = BitmapFactory.Options()
                op.inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.absolutePath, op)

                val met = resources.displayMetrics
                op.inJustDecodeBounds = false
                op.inSampleSize = Math.max(
                    op.outWidth / met.widthPixels,
                    op.outHeight / met.heightPixels)

                BitmapFactory.decodeFile(file.absolutePath, op)
            }.ui {
                imageView.setImageBitmap(it)
            }
        }
    }

    init {
        addView(imageView)
        addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL))
    }
}
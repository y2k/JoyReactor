package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.decodeBitmapFile
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by y2k on 2/29/16.
 */
class ProgressImageView(
    context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val imageView = ImageView(context)
    private val progress = ProgressBar(context, null, android.R.attr.progressBarStyle)

    var image by Delegates.observable<File?>(null) { prop, old, new ->
        if (old != new) reload(new)
    }

    private fun reload(file: File?) {
        async_ {
            if (file == null) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE

                val m = resources.displayMetrics
                val bitmap = await(decodeBitmapFile(file, m.widthPixels, m.heightPixels))
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    init {
        addView(imageView)
        addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL))
    }
}
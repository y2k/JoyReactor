package y2k.joyreactor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.LinksPool
import y2k.joyreactor.services.ImageService
import kotlin.properties.Delegates

/**
 * Created by y2k on 9/26/15.
 */
class WebImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    val imageService = ServiceLocator.resolve<ImageService>()
    var image by Delegates.observable(null as Image?) { prop, old, new -> invalidate() }

    private var state = ImageState()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode) return

        if (state.image != image || state.width != canvas.width || state.height != canvas.height) {
            state = ImageState(image, canvas.width, canvas.height)

            val url = imageService.makeUrl(state.image, state.width, state.height)
            imageService.to<Bitmap>(LinksPool.default, url, this).subscribe { setImageBitmap(it) }
        }
    }

    private data class ImageState(val image: Image? = null, val width: Int = 0, val height: Int = 0)
}
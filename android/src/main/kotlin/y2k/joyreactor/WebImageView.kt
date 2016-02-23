package y2k.joyreactor

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.ImageRequest

/**
 * Created by y2k on 9/26/15.
 */
class WebImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    private val invalidator = Invalidator()

    fun setImage(image: Image?) {
        invalidator.invalidateStateChanges(image ?: EMPTY)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        invalidator.invalidateStateChanges(null)
    }

    private inner class Invalidator {

        private var image: Image? = null
        private var lastWidth: Int = 0
        private var lastHeight: Int = 0

        fun invalidateStateChanges(newImage: Image?) {
            if (newImage === EMPTY) {
                invalidateToEmpty()
            } else {
                if (newImage == null)
                    invalidateOnChangeSize()
                else
                    invalidateOnImageChanged(newImage)
            }
        }

        private fun invalidateToEmpty() {
            if (this.image === EMPTY) return
            setImageDrawable(null)
            ImageRequest().setUrl(null).to(this) { bitmap -> Unit }
        }

        private fun invalidateOnChangeSize() {
            if (image == null) return
            if (lastWidth == width && lastHeight == height) return

            lastWidth = width
            lastHeight = height

            setImageDrawable(null)
            ImageRequest()
                .setUrl(image)
                .setSize(lastWidth, lastHeight)
                .to(this) { setImageBitmap(it) }
        }

        private fun invalidateOnImageChanged(newImage: Image) {
            if (image != null && image == newImage) return

            image = newImage

            if (lastWidth == 0 || lastHeight == 0) return

            lastWidth = width
            lastHeight = height

            setImageDrawable(null)
            ImageRequest()
                .setUrl(image)
                .setSize(lastWidth, lastHeight)
                .to(this) { setImageBitmap(it) }
        }
    }

    companion object {

        private val EMPTY = Image("", 0, 0)
    }
}
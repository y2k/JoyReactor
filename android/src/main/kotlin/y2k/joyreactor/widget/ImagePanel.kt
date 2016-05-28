package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import y2k.joyreactor.R
import y2k.joyreactor.WebImageView
import y2k.joyreactor.common.forEachChild
import y2k.joyreactor.common.inflateToSelf
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.model.Image
import java.util.*

/**
 * Created by y2k on 30/11/15.
 */
class ImagePanel(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val imageViews = ArrayList<WebImageView>()

    init {
        inflateToSelf(R.layout.layout_post_images)
        loadImageViews()
    }

    private fun loadImageViews() {
        val root = getChildAt(0) as ViewGroup
        root.forEachChild {
            imageViews.add((it as ViewGroup).getChildAt(0) as WebImageView)
        }
    }

    fun setImages(images: List<Image>) {
        for (i in 0..Math.min(images.size, imageViews.size) - 1)
            imageViews[i].image = images[i]
        imageViews.forEachIndexed { i, view -> view.isVisible = i < images.size }
    }
}
package y2k.joyreactor.platform

import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIGraphics
import org.robovm.apple.uikit.UIImage
import org.robovm.apple.uikit.UIScreen
import y2k.joyreactor.common.images.BaseImageRequest

import java.io.File

/**
 * Created by y2k on 12/10/15.
 */
class ImageRequest : BaseImageRequest<UIImage>() {

    override fun decode(path: File): UIImage {
        val image = UIImage(path)
        forceLoadInMemory(image)
        return image
    }

    private fun forceLoadInMemory(image: UIImage) {
        UIGraphics.beginImageContext(CGSize(100.0, 100.0))
        image.draw(CGPoint.Zero())
        UIGraphics.endImageContext()
    }

    override fun setSize(width: Int, height: Int): BaseImageRequest<UIImage> {
        val scale = Math.min(2.0, UIScreen.getMainScreen().scale)
        return super.setSize((scale * width).toInt(), (scale * height).toInt())
    }
}
package y2k.joyreactor

import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIActivityIndicatorView
import org.robovm.apple.uikit.UIImage
import org.robovm.apple.uikit.UIViewAutoresizing
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.ImageViewModel
import java.io.File

/**
 * Created by y2k on 10/25/15.
 */
@CustomClass("ImageViewController")
class ImageViewController : UIViewController() {

    @IBOutlet lateinit var indicatorView: UIActivityIndicatorView

    override fun viewDidLoad() {
        super.viewDidLoad()

        val vm = ServiceLocator.resolve<ImageViewModel>()
        bindingBuilder {
            indicatorView(indicatorView, vm.isBusy)
            action(vm.imageFile) {
                if (it == null) return@action
                val scrollView = ImageScrollView(view.frame)
                // TODO: вынести в ImageScrollView
                scrollView.autoresizingMask = UIViewAutoresizing(((1 shl 1) + (1 shl 4)).toLong())
                view.addSubview(scrollView)
                scrollView.displayTiledImageNamed(it.absolutePath, getImageSize(it))
            }
        }
    }

    fun getImageSize(imageFile: File): CGSize {
        return UIImage.getImage(imageFile).size
    }
}
package y2k.joyreactor

import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIActivityIndicatorView
import org.robovm.apple.uikit.UIImage
import org.robovm.apple.uikit.UIViewAutoresizing
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.ImagePresenter

import java.io.File

/**
 * Created by y2k on 10/25/15.
 */
@CustomClass("ImageViewController")
class ImageViewController : UIViewController() {

    @IBOutlet lateinit var indicatorView: UIActivityIndicatorView

    override fun viewDidLoad() {
        super.viewDidLoad()

        ServiceLocator.resolve(object : ImagePresenter.View {

            override fun setBusy(isBusy: Boolean) {
                // FIXME:
                //        if (isBusy) indicatorView.startAnimating();
                //        else indicatorView.stopAnimating();
            }

            override fun showImage(imageFile: File) {
                val scrollView = ImageScrollView(view.frame)
                scrollView.autoresizingMask = UIViewAutoresizing(((1 shl 1) + (1 shl 4)).toLong())
                view.addSubview(scrollView)
                scrollView.displayTiledImageNamed(imageFile.absolutePath, getImageSize(imageFile))
            }
        })
    }

    private fun getImageSize(imageFile: File): CGSize {
        return UIImage(imageFile).size
    }
}
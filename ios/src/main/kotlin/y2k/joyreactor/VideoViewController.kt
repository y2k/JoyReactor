package y2k.joyreactor

import org.robovm.apple.avfoundation.AVLayerVideoGravity
import org.robovm.apple.avfoundation.AVPlayer
import org.robovm.apple.avfoundation.AVPlayerLayer
import org.robovm.apple.coremedia.CMTime
import org.robovm.apple.foundation.NSObject
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.uikit.UIActivityIndicatorView
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.VideoViewModel

/**
 * Created by y2k on 22/10/15.
 */
@CustomClass("VideoViewController")
class VideoViewController : UIViewController() {

    @IBOutlet lateinit var indicatorView: UIActivityIndicatorView

    lateinit var player: AVPlayer
    var repeatObserver: NSObject? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        navigationController.setNavigationBarHidden(true, true)

        val vm = ServiceLocator.resolve<VideoViewModel>()
        bindingBuilder {
            action(vm.isBusy) {
                navigationItem.setHidesBackButton(it, true)
                if (it) indicatorView.startAnimating()
                else indicatorView.stopAnimating()
            }
            action(vm.videoFile) {
                if (it == null) return@action

                // TODO: вынести в отдельный контрол
                player = AVPlayer(NSURL(it))
                repeatObserver = player.addBoundaryTimeObserver(
                    listOf(player.currentItem.asset.duration.subtract(CMTime.create(0.1, 600))),
                    null) { player.seekToTime(CMTime.Zero()) }

                val layer = AVPlayerLayer()
                layer.player = player
                layer.frame = view.frame
                layer.videoGravity = AVLayerVideoGravity.ResizeAspect

                view.layer.addSublayer(layer)
                player.play()
            }
        }
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        navigationController.setHidesBarsOnTap(true)
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        navigationController.setHidesBarsOnTap(false)
        if (repeatObserver != null) player.removeTimeObserver(repeatObserver)
    }
}
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
import y2k.joyreactor.presenters.VideoPresenter
import java.io.File

/**
 * Created by y2k on 22/10/15.
 */
@CustomClass("VideoViewController")
class VideoViewController : UIViewController(), VideoPresenter.View {

    @IBOutlet
    lateinit var indicatorView: UIActivityIndicatorView

    lateinit var player: AVPlayer
    var repeatObserver: NSObject? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        ServiceLocator.resolve(this)
        navigationController.setNavigationBarHidden(true, true)
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

    override fun showVideo(videoFile: File) {
        player = AVPlayer(NSURL(videoFile))
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

    override fun setBusy(isBusy: Boolean) {
        navigationItem.setHidesBackButton(isBusy, true)
        if (isBusy)
            indicatorView.startAnimating()
        else
            indicatorView.stopAnimating()
    }
}
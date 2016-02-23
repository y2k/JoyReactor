package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UIImageView
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.translate
import y2k.joyreactor.model.Profile
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.presenters.ProfilePresenter

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("ProfileViewController")
class ProfileViewController : UIViewController(), ProfilePresenter.View {

    @IBOutlet
    lateinit var userImage: UIImageView
    @IBOutlet
    lateinit var userName: UILabel
    @IBOutlet
    lateinit var rating: UILabel
    @IBOutlet
    lateinit var stars: StarProgress
    @IBOutlet
    lateinit var progressToNewStar: ProgressBar
    @IBOutlet
    lateinit var logoutButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()
        val presenter = ServiceLocator.resolve(this)
        logoutButton.addOnTouchUpInsideListener { sender, e -> presenter.logout() }
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    override fun setProfile(profile: Profile) {
        userName.text = profile.userName
        ImageRequest()
            .setUrl(profile.userImage)
            .setSize(userImage.frame.width.toInt(), userImage.frame.height.toInt())
            .to(userImage) { userImage.image = it }
        rating.text = "Rating: ".translate() + profile.rating
        stars.setStars(profile.stars)
        progressToNewStar.setValue(profile.progressToNewStar)
    }

    override fun setBusy(isBusy: Boolean) {
        logoutButton.isEnabled = !isBusy
        navigationItem.setHidesBackButton(isBusy, true)
    }
}
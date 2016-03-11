package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UIImageView
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.translate
import y2k.joyreactor.viewmodel.ProfileViewModel

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("ProfileViewController")
class ProfileViewController : UIViewController() {

    @IBOutlet lateinit var userImage: UIImageView
    @IBOutlet lateinit var userName: UILabel
    @IBOutlet lateinit var rating: UILabel
    @IBOutlet lateinit var stars: StarProgress
    @IBOutlet lateinit var progressToNewStar: ProgressBar
    @IBOutlet lateinit var logoutButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        val vm = ServiceLocator.resolve<ProfileViewModel>()
        bindingBuilder {
            action(vm.isBusy) {
                logoutButton.isEnabled = !it
                navigationItem.setHidesBackButton(it, true)
            }

            label(userName, vm.userName)
            imageView(userImage, vm.userImage)
            label(rating, vm.rating, { "Rating: ".translate() + it })
            progressBar(progressToNewStar, vm.nextStarProgress)
            starProgress(stars, vm.stars)

            command(logoutButton) { vm.logout() }
        }
    }
}
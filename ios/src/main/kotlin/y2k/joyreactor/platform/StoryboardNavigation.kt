package y2k.joyreactor.platform

import org.robovm.apple.foundation.NSMutableArray
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UINavigationController
import org.robovm.apple.uikit.UIViewController
import y2k.joyreactor.ImageViewController

/**
 * Created by y2k on 02/10/15.
 */
class StoryboardNavigation : Navigation {

    override fun switchProfileToLogin() {
        switchTo("Login")
    }

    override fun switchLoginToProfile() {
        switchTo("Profile")
    }

    override fun closeCreateComment() {
        navigationController.popViewController(true)
    }

    override fun closeAddTag() {
        navigationController.popViewController(true)
    }

    override fun openPost(postId: String) {
        sPostIdArgument = postId
        navigationController.pushViewController(instantiateViewController("Post"), true)
    }

    override val argumentPostId: String
        get() = sPostIdArgument!!

    private fun switchTo(storyboardId: String) {
        val stack = NSMutableArray(navigationController.viewControllers)
        stack.removeAt(stack.size - 1)
        stack.add(instantiateViewController(storyboardId))
        navigationController.setViewControllers(stack, true)
    }

    private fun instantiateViewController(storyboardId: String): UIViewController {
        return navigationController.storyboard.instantiateViewController(storyboardId)
    }

    override fun openBrowser(url: String) {
        UIApplication.getSharedApplication().openURL(NSURL(url))
    }

    override fun openVideo(postId: String) {
        sPostIdArgument = postId
        navigationController.pushViewController(instantiateViewController("Video"), true)
    }

    override fun openImageView(postId: String) {
        sPostIdArgument = postId
        navigationController.pushViewController(ImageViewController(), true)
    }

    override fun openCreateComment() {
        // TODO:
    }

    override fun openPostGallery() {
        // TODO:
    }

    companion object {

        private var sPostIdArgument: String? = null // TODO:

        val navigationController: UINavigationController
            get() = UIApplication.getSharedApplication().windows[0].rootViewController as UINavigationController
    }
}
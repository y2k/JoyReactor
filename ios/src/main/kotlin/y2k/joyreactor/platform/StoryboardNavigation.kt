package y2k.joyreactor.platform

import org.robovm.apple.foundation.NSMutableArray
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UINavigationController
import org.robovm.apple.uikit.UIViewController
import kotlin.reflect.KClass

/**
 * Created by y2k on 02/10/15.
 */
class StoryboardNavigation : NavigationService {

    override fun close() {
        throw UnsupportedOperationException()
    }

    override fun <T : Any> open(vmType: KClass<T>, argument: String) {
        throw UnsupportedOperationException()
    }

    //    override fun openMessages(name: String) {
//        sArgument = name
//        navigationController.pushViewController(instantiateViewController("Messages"), true)
//    }
//
//    override fun switchProfileToLogin() {
//        switchTo("Login")
//    }
//
//    override fun switchLoginToProfile() {
//        switchTo("Profile")
//    }
//
//    override fun closeCreateComment() {
//        navigationController.popViewController(true)
//    }
//
//    override fun closeAddTag() {
//        navigationController.popViewController(true)
//    }
//
//    override fun openPost(postId: String) {
//        sArgument = postId
//        navigationController.pushViewController(instantiateViewController("Post"), true)
//    }

    override val argument: String
        get() = sArgument!!

    private fun switchTo(storyboardId: String) {
        val stack = NSMutableArray(navigationController.viewControllers.toList())
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

//    override fun openVideo(postId: String) {
//        sArgument = postId
//        navigationController.pushViewController(instantiateViewController("Video"), true)
//    }
//
//    override fun openImageView(postId: String) {
//        sArgument = postId
//        navigationController.pushViewController(ImageViewController(), true)
//    }
//
//    override fun openCreateComment() {
//        // TODO:
//    }
//
//    override fun openPostGallery() {
//        // TODO:
//    }

    companion object {

        private var sArgument: String? = null // TODO:

        val navigationController: UINavigationController
            get() = UIApplication.getSharedApplication().windows[0].rootViewController as UINavigationController
    }
}
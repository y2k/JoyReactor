package y2k.joyreactor

import org.robovm.apple.uikit.UIAlertAction
import org.robovm.apple.uikit.UIAlertActionStyle
import org.robovm.apple.uikit.UIAlertController
import org.robovm.apple.uikit.UIViewController

/**
 * Created by y2k on 11/1/15.
 */
class MenuController(private val parent: UIViewController) {

    private val alert = UIAlertController()

    fun addNavigation(title: String, storyboardId: String): MenuController {
        alert.addAction(UIAlertAction(title, UIAlertActionStyle.Default) {
            parent.navigationController.pushViewController(
                parent.storyboard.instantiateViewController(storyboardId), true)
        })
        return this
    }

    fun addCancel(title: String): MenuController {
        alert.addAction(UIAlertAction(title, UIAlertActionStyle.Cancel, null))
        return this
    }

    fun present() {
        parent.presentViewController(alert, true, null)
    }
}
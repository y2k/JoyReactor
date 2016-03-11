package y2k.joyreactor

import org.robovm.apple.uikit.UIActivityIndicatorView
import org.robovm.apple.uikit.UIStatusBarStyle
import org.robovm.apple.uikit.UITextField
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.AddTagViewModel

/**
 * Created by y2k on 08/10/15.
 */
@CustomClass("AddTagViewController")
class AddTagViewController : UIViewController() {

    @IBOutlet lateinit var tagNameView: UITextField
    @IBOutlet lateinit var activityView: UIActivityIndicatorView

    override fun viewDidLoad() {
        super.viewDidLoad()

        val vm = ServiceLocator.resolve<AddTagViewModel>()
        bindingBuilder(this) {
            action(vm.isBusy) {
                navigationItem.rightBarButtonItem.isEnabled = !it
                navigationItem.setHidesBackButton(it, true)
                if (it) activityView.startAnimating()
                else activityView.stopAnimating()
            }
            navigationItem {
                rightCommand { vm.add() }
            }
            textField(tagNameView, vm.tag)
        }
        tagNameView.becomeFirstResponder()
    }

    override fun getPreferredStatusBarStyle(): UIStatusBarStyle {
        return UIStatusBarStyle.LightContent
    }
}
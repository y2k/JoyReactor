package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITextField
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.LoginViewModel

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("LoginViewController")
class LoginViewController : UIViewController() {

    @IBOutlet lateinit var username: UITextField
    @IBOutlet lateinit var password: UITextField
    @IBOutlet lateinit var loginButton: UIButton
    @IBOutlet lateinit var registerButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        val vm = ServiceLocator.resolve<LoginViewModel>()
        bindingBuilder {
            focusOrder(username, password)
            action(vm.isBusy) {
                navigationItem.setHidesBackButton(it)
                loginButton.isEnabled = !it
            }
            textField(username, vm.username)
            textField(password, vm.password)
            command(loginButton, { vm.login() })
            command(registerButton, { vm.register() })
        }
    }
}
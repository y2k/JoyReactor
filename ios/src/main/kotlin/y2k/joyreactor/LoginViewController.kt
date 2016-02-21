package y2k.joyreactor

import org.robovm.apple.foundation.NSURL
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.LoginPresenter

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("LoginViewController")
class LoginViewController : UIViewController(), LoginPresenter.View {

    @IBOutlet lateinit var username: UITextField
    @IBOutlet lateinit var password: UITextField
    @IBOutlet lateinit var loginButton: UIButton
    @IBOutlet lateinit var registerButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()
        val presenter = ServiceLocator.resolve(this)

        loginButton.addOnTouchUpInsideListener { sender, e ->
            presenter.login(username.text, password.text)
        }
        registerButton.addOnTouchUpInsideListener { sender, e -> presenter.register() }

        username.delegate = DefaultUITextFieldDelegate(password)
        password.delegate = DefaultUITextFieldDelegate()
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    override fun setBusy(isBusy: Boolean) {
        navigationItem.setHidesBackButton(isBusy)
        loginButton.isEnabled = !isBusy
    }

    override fun showError() {
        // TODO:
    }

    override fun openUrl(url: String) {
        UIApplication.getSharedApplication().openURL(NSURL(url))
    }

    // ==========================================
    // Outlets
    // ==========================================

    private class DefaultUITextFieldDelegate(val next: UITextField? = null) : UITextFieldDelegateAdapter() {

        override fun shouldReturn(textField: UITextField): Boolean {
            if (next == null) textField.resignFirstResponder()
            else next.becomeFirstResponder()
            return true
        }
    }
}
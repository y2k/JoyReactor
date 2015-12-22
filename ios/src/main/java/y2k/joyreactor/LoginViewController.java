package y2k.joyreactor;

import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.LoginPresenter;

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("LoginViewController")
public class LoginViewController extends UIViewController implements LoginPresenter.View {

    UITextField username;
    UITextField password;
    UIButton loginButton;
    UIButton registerButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        LoginPresenter presenter = ServiceLocator.getInstance().provideLoginPresenter(this);

        loginButton.addOnTouchUpInsideListener((sender, e) -> presenter.login(
                username.getText(), password.getText()));
        registerButton.addOnTouchUpInsideListener((sender, e) -> presenter.register());

        username.setDelegate(new DefaultUITextFieldDelegate(password));
        password.setDelegate(new DefaultUITextFieldDelegate());
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    @Override
    public void setBusy(boolean isBusy) {
        getNavigationItem().setHidesBackButton(isBusy);
        loginButton.setEnabled(!isBusy);
    }

    @Override
    public void showError() {
        // TODO:
    }

    @Override
    public void openUrl(String url) {
        UIApplication.getSharedApplication().openURL(new NSURL(url));
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setUsername(UITextField username) {
        this.username = username;
    }

    @IBOutlet
    void setPassword(UITextField password) {
        this.password = password;
    }

    @IBOutlet
    void setLoginButton(UIButton loginButton) {
        this.loginButton = loginButton;
    }

    @IBOutlet
    void setRegisterButton(UIButton registerButton) {
        this.registerButton = registerButton;
    }

    private static class DefaultUITextFieldDelegate extends UITextFieldDelegateAdapter {

        private UITextField next;

        public DefaultUITextFieldDelegate(UITextField next) {
            this.next = next;
        }

        public DefaultUITextFieldDelegate() {
        }

        @Override
        public boolean shouldReturn(UITextField textField) {
            if (next == null) textField.resignFirstResponder();
            else next.becomeFirstResponder();
            return true;
        }
    }
}
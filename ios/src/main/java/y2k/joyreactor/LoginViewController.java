package y2k.joyreactor;

import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

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
        LoginPresenter presenter = new LoginPresenter(this);

        loginButton.addOnTouchUpInsideListener((sender, e) -> presenter.login());
        registerButton.addOnTouchUpInsideListener((sender, e) -> presenter.register());

        username.setDelegate(new DefaultUITextFieldDelegate());
        password.setDelegate(new DefaultUITextFieldDelegate());
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    @Override
    public String getUsername() {
        return username.getText();
    }

    @Override
    public String getPassword() {
        return password.getText();
    }

    @Override
    public void setProgress(boolean isProgress) {
        getNavigationItem().setHidesBackButton(isProgress);
        loginButton.setEnabled(!isProgress);
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

        @Override
        public boolean shouldReturn(UITextField textField) {
            textField.resignFirstResponder();
            return true;
        }
    }
}
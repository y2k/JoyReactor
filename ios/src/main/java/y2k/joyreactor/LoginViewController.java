package y2k.joyreactor;

import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIViewController;
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

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        LoginPresenter presenter = new LoginPresenter(this);
        loginButton.addOnTouchUpInsideListener((sender, e) -> presenter.login());
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
ы
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
}
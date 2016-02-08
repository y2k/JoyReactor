package y2k.joyreactor;

import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.CreateCommentPresenter;

/**
 * Created by y2k on 10/4/15.
 */
@CustomClass("CreateCommentViewController")
public class CreateCommentViewController extends UIViewController implements CreateCommentPresenter.View {

    UITextView commentTextView;
    UIButton sendButton;
    UIButton cancelButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        CreateCommentPresenter presenter = ServiceLocator.INSTANCE.resolve(this);
        cancelButton.addOnTouchUpInsideListener(
                (sender, e) -> getNavigationController().popViewController(true));
        sendButton.addOnTouchUpInsideListener(
                (sender, e) -> presenter.create(commentTextView.getText()));
    }

    // ==========================================
    // View methods
    // ==========================================

    @Override
    public void setIsBusy(boolean isBusy) {
        getNavigationItem().setHidesBackButton(isBusy, true);
    }

    @Override
    public void setUser(Profile profile) {
        // TODO
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setCommentTextView(UITextView commentTextView) {
        this.commentTextView = commentTextView;
    }

    @IBOutlet
    void setSendButton(UIButton sendButton) {
        this.sendButton = sendButton;
    }

    @IBOutlet
    void setCancelButton(UIButton cancelButton) {
        this.cancelButton = cancelButton;
    }
}
package y2k.joyreactor;

import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

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

        CreateCommentPresenter presenter = new CreateCommentPresenter(this);
        cancelButton.addOnTouchUpInsideListener(
                (sender, e) -> getNavigationController().popViewController(true));
        sendButton.addOnTouchUpInsideListener(
                (sender, e) -> presenter.create());
    }

    // ==========================================
    // View methods
    // ==========================================

    @Override
    public String getCommentText() {
        return commentTextView.getText();
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        getNavigationItem().setHidesBackButton(isBusy, true);
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
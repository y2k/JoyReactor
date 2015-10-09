package y2k.joyreactor;

import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

/**
 * Created by y2k on 08/10/15.
 */
@CustomClass("AddTagViewController")
public class AddTagViewController extends UIViewController implements AddTagPresenter.View {

    UITextField tagNameView;
    UIActivityIndicatorView activityView;
    UIBarButtonItem cancelButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        AddTagPresenter presenter = new AddTagPresenter(this);

        cancelButton.setOnClickListener(sender -> {
            tagNameView.resignFirstResponder();
            dismissViewController(true, null);
        });

        tagNameView.setDelegate(new UITextFieldDelegateAdapter() {

            @Override
            public boolean shouldReturn(UITextField textField) {
                presenter.addTag();
                return false;
            }
        });

        tagNameView.becomeFirstResponder();
        activityView.stopAnimating();
    }

    @Override
    public String getTagName() {
        return tagNameView.getText();
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        cancelButton.setEnabled(!isBusy);
        if (isBusy) activityView.startAnimating();
        else activityView.stopAnimating();
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        return UIStatusBarStyle.LightContent;
    }

    @IBOutlet
    void setTagNameView(UITextField tagNameView) {
        this.tagNameView = tagNameView;
    }

    @IBOutlet
    void setActivityView(UIActivityIndicatorView activityView) {
        this.activityView = activityView;
    }

    @IBOutlet
    void setCancelButton(UIBarButtonItem cancelButton) {
        this.cancelButton = cancelButton;
    }
}
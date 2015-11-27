package y2k.joyreactor;

import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.presenters.AddTagPresenter;

/**
 * Created by y2k on 08/10/15.
 */
@CustomClass("AddTagViewController")
public class AddTagViewController extends UIViewController implements AddTagPresenter.View {

    UITextField tagNameView;
    UIActivityIndicatorView activityView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        AddTagPresenter presenter = new AddTagPresenter(this);

        getNavigationItem().getRightBarButtonItem()
                .setOnClickListener(sender -> presenter.add(tagNameView.getText()));

        tagNameView.setDelegate(new UITextFieldDelegateAdapter() {

            @Override
            public boolean shouldReturn(UITextField textField) {
                presenter.add(tagNameView.getText());
                return false;
            }
        });

        tagNameView.becomeFirstResponder();
        activityView.stopAnimating();
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        getNavigationItem().getRightBarButtonItem().setEnabled(!isBusy);
        getNavigationItem().setHidesBackButton(isBusy, true);
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
}
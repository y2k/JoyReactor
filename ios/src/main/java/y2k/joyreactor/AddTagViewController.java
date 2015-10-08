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
    UIButton createButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        AddTagPresenter presenter = new AddTagPresenter(this);

        tagNameView.setDelegate(new UITextFieldDelegateAdapter() {

            @Override
            public boolean shouldReturn(UITextField textField) {
                textField.becomeFirstResponder();
                return false;
            }
        });
//        createButton.addOnTouchUpInsideListener((sender, e) -> presenter.addTag());
    }

    @Override
    public String getTagName() {
        return tagNameView.getText();
    }

    @IBOutlet
    void setTagNameView(UITextField tagNameView) {
        this.tagNameView = tagNameView;
    }
}
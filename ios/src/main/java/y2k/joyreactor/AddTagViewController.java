package y2k.joyreactor;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;

/**
 * Created by y2k on 08/10/15.
 */
@CustomClass("AddTagViewController")
public class AddTagViewController extends UIViewController implements AddTagPresenter.View {

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        AddTagPresenter presenter = new AddTagPresenter(this);
    }

    @Override
    public String getTagName() {
        return null;
    }
}
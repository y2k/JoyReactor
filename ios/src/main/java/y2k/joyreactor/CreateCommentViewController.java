package y2k.joyreactor;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;

/**
 * Created by y2k on 10/4/15.
 */
@CustomClass("CreateCommentViewController")
public class CreateCommentViewController extends UIViewController implements CreateCommentPresenter.View {

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new CreateCommentPresenter(this);
    }

    // ==========================================
    // View methods
    // ==========================================

    // ==========================================
    // Outlets
    // ==========================================
}
package y2k.joyreactor;

import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIViewController;

/**
 * Created by y2k on 11/1/15.
 */
public class MenuController {

    private UIAlertController alert = new UIAlertController();
    private UIViewController parent;

    public MenuController(UIViewController parent) {
        this.parent = parent;
    }

    public MenuController addNavigation(String title, String storyboardId) {
        alert.addAction(new UIAlertAction(title, UIAlertActionStyle.Default, s ->
                parent.getNavigationController().pushViewController(
                        parent.getStoryboard().instantiateViewController(storyboardId), true)));
        return this;
    }

    public MenuController addCancel(String title) {
        alert.addAction(new UIAlertAction(title, UIAlertActionStyle.Cancel, null));
        return this;
    }

    public void present() {
        parent.presentViewController(alert, true, null);
    }
}
package y2k.joyreactor.platform;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIViewController;
import y2k.joyreactor.Navigation;

/**
 * Created by y2k on 02/10/15.
 */
public class StoryboardNavigation extends Navigation {

    @Override
    public void switchProfileToLogin() {
        switchTo("Login");
    }

    @Override
    public void switchLoginToProfile() {
        switchTo("Profile");
    }

    @Override
    public void closeCreateComment() {
        getNavigationController().popViewController(true);
    }

    private void switchTo(String storyboardId) {
        NSArray<UIViewController> stack = new NSMutableArray<>(getNavigationController().getViewControllers());
        stack.remove(stack.size() - 1);
        stack.add(getNavigationController().getStoryboard().instantiateViewController(storyboardId));
        getNavigationController().setViewControllers(stack, true);
    }

    private UINavigationController getNavigationController() {
        return (UINavigationController) UIApplication
                .getSharedApplication()
                .getWindows().get(0)
                .getRootViewController();
    }
}
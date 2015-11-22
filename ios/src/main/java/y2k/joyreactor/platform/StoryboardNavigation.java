package y2k.joyreactor.platform;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIViewController;
import y2k.joyreactor.ImageViewController;
import y2k.joyreactor.Navigation;
import y2k.joyreactor.Post;

/**
 * Created by y2k on 02/10/15.
 */
public class StoryboardNavigation extends Navigation {

    private static Post sPostArgument; // TODO:
    private static String sPostIdArgument; // TODO:

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

    @Override
    public void closeAddTag() {
        getNavigationController().popViewController(true);
    }

    @Override
    public void openPost(Post post) {
        sPostArgument = post;
        getNavigationController().pushViewController(instantiateViewController("Post"), true);
    }

    @Override
    public Post getArgumentPost() {
        return sPostArgument;
    }

    @Override
    public void openPost(String postId) {
        sPostIdArgument = postId;
        getNavigationController().pushViewController(instantiateViewController("Post"), true);
    }

    @Override
    public String getArgumentPostId() {
        return sPostIdArgument;
    }

    private void switchTo(String storyboardId) {
        NSArray<UIViewController> stack = new NSMutableArray<>(getNavigationController().getViewControllers());
        stack.remove(stack.size() - 1);
        stack.add(instantiateViewController(storyboardId));
        getNavigationController().setViewControllers(stack, true);
    }

    private UIViewController instantiateViewController(String storyboardId) {
        return getNavigationController()
                .getStoryboard()
                .instantiateViewController(storyboardId);
    }

    public static UINavigationController getNavigationController() {
        return (UINavigationController) UIApplication
                .getSharedApplication()
                .getWindows().get(0)
                .getRootViewController();
    }

    @Override
    public void openBrowser(String url) {
        UIApplication.getSharedApplication().openURL(new NSURL(url));
    }

    @Override
    public void openVideo(Post post) {
        sPostArgument = post;
        getNavigationController().pushViewController(instantiateViewController("Video"), true);
    }

    @Override
    public void openImageView(Post post) {
        sPostArgument = post;
        getNavigationController().pushViewController(new ImageViewController(), true);
    }
}
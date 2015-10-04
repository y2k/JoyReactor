package y2k.joyreactor;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.*;

/**
 * Created by y2k on 9/26/15.
 */
public class SideMenu {

    static final float PanelWidth = 270;

    private final UIViewController parent;
    private final UIView parentView;

    private final UIButton closeButton;
    private final UIView menuView;

    private final UIViewController menuController;

    public SideMenu(UIViewController parent, String menuStoryboardId) {
        this.parent = parent;
        parentView = parent.getNavigationController().getView();
        menuController = parent.getStoryboard().instantiateViewController(menuStoryboardId);
        menuView = menuController.getView();

        closeButton = new UIButton(parentView.getFrame());
        closeButton.addOnTouchUpInsideListener((sender, e) -> closeButtonClicked());
    }

    void closeButtonClicked() {
        UIView.animate(0.3,
                this::restoreViewPosition,
                s -> {
                    removeMenuViews();
                    menuController.viewDidDisappear(false);
                });
    }

    public void attach() {
        UIBarButtonItem menuButton = new UIBarButtonItem();
        menuButton.setImage(UIImage.getImage("MenuIcon.png"));
        menuButton.setOnClickListener(sender -> menuButtonClicked());
        parent.getNavigationItem().setLeftBarButtonItem(menuButton);

        UIScreenEdgePanGestureRecognizer edgeGesture = new UIScreenEdgePanGestureRecognizer(s -> menuButtonClicked());
        edgeGesture.setEdges(UIRectEdge.Left);
        parent.getView().addGestureRecognizer(edgeGesture);
    }

    void menuButtonClicked() {
        if (menuView.getSuperview() != null)
            return;

        CGRect menuFrame = parentView.getFrame();
        menuFrame.getSize().setWidth(PanelWidth);
        menuView.setFrame(menuFrame);
        parentView.addSubview(menuView);
        parentView.sendSubviewToBack(menuView);
        menuFrame.getOrigin().setX(-PanelWidth);

        parentView.addSubview(closeButton);

        UIView.animate(0.3, () -> {
            menuView.setFrame(menuFrame);
            for (UIView s : parentView.getSubviews()) {
                CGRect f = s.getFrame();
                s.setFrame(f.offset(PanelWidth, 0));
            }
        }, s -> menuController.viewWillAppear(false));
    }

    public void deactive() {
        if (closeButton.getSuperview() == null)
            return;
        restoreViewPosition();
        removeMenuViews();
    }

    void restoreViewPosition() {
        for (UIView s : parentView.getSubviews()) {
            if (s == menuView)
                continue;
            CGRect f = s.getFrame();
            s.setFrame(f.offset(-PanelWidth, 0));
        }
    }

    void removeMenuViews() {
        closeButton.removeFromSuperview();
        menuView.removeFromSuperview();
    }
}
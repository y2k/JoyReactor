package y2k.joyreactor;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIView;

/**
 * Created by y2k on 11/1/15.
 */
class BottomButton {

    private UIView view;

    BottomButton(UIView view) {
        this.view = view;
    }

    void setHidden(boolean hidden) {
        if (hidden) {
            UIView.animate(0.3,
                    () -> {
                        CGRect f = view.getFrame();
                        f.setY(view.getSuperview().getFrame().getHeight());
                        view.setFrame(f);
                        view.setAlpha(0);
                    },
                    s -> view.setHidden(true));
        } else {
            view.setHidden(false);
            UIView.animate(0.3,
                    () -> {
                        CGRect f = view.getFrame();
                        f.setY(view.getSuperview().getFrame().getHeight() - f.getHeight());
                        view.setFrame(f);
                        view.setAlpha(1);
                    });
        }
    }
}
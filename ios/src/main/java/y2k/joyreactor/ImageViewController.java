package y2k.joyreactor;

import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.presenters.ImagePresenter;

import java.io.File;

/**
 * Created by y2k on 10/25/15.
 */
@CustomClass("ImageViewController")
public class ImageViewController extends UIViewController implements ImagePresenter.View {

    @IBOutlet
    UIActivityIndicatorView indicatorView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new ImagePresenter(this);
    }

    @Override
    public void setBusy(boolean isBusy) {
        if (isBusy) indicatorView.startAnimating();
        else indicatorView.stopAnimating();
    }

    @Override
    public void showImage(File imageFile) {
        // TODO:
    }
}
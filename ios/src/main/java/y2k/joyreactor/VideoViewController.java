package y2k.joyreactor;

import org.robovm.apple.avfoundation.AVLayerVideoGravity;
import org.robovm.apple.avfoundation.AVPlayer;
import org.robovm.apple.avfoundation.AVPlayerLayer;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.presenters.VideoPresenter;

import java.io.File;

/**
 * Created by y2k on 22/10/15.
 */
@CustomClass("VideoViewController")
public class VideoViewController extends UIViewController implements VideoPresenter.View {

    UIActivityIndicatorView indicatorView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new VideoPresenter(this);
    }

    @Override
    public void showVideo(File videoFile) {
        AVPlayer player = new AVPlayer(new NSURL(videoFile.getAbsolutePath()));
        AVPlayerLayer layer = new AVPlayerLayer();

        layer.setPlayer(player);
        layer.setVideoGravity(AVLayerVideoGravity.ResizeAspectFill);

        getView().getLayer().addSublayer(layer);
        player.play();
    }

    @Override
    public void setBusy(boolean isBusy) {
        getNavigationItem().setHidesBackButton(isBusy, true);
        if (isBusy) indicatorView.startAnimating();
        else indicatorView.stopAnimating();
    }

    @IBOutlet
    void setIndicatorView(UIActivityIndicatorView indicatorView) {
        this.indicatorView = indicatorView;
    }
}
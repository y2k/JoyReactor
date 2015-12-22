package y2k.joyreactor;

import org.robovm.apple.avfoundation.AVLayerVideoGravity;
import org.robovm.apple.avfoundation.AVPlayer;
import org.robovm.apple.avfoundation.AVPlayerLayer;
import org.robovm.apple.coremedia.CMTime;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.VideoPresenter;

import java.io.File;
import java.util.Collections;

/**
 * Created by y2k on 22/10/15.
 */
@CustomClass("VideoViewController")
public class VideoViewController extends UIViewController implements VideoPresenter.View {

    UIActivityIndicatorView indicatorView;
    NSObject repeatObserver;
    AVPlayer player;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        ServiceLocator.getInstance().provideVideoPresenter(this);
        getNavigationController().setNavigationBarHidden(true, true);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getNavigationController().setHidesBarsOnTap(true);
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);
        getNavigationController().setHidesBarsOnTap(false);
        if (repeatObserver != null) player.removeTimeObserver(repeatObserver);
    }

    @Override
    public void showVideo(File videoFile) {
        player = new AVPlayer(new NSURL(videoFile));
        repeatObserver = player.addBoundaryTimeObserver(
                Collections.singletonList(player.getCurrentItem().getAsset().getDuration().subtract(CMTime.create(0.1, 600))),
                null, () -> player.seekToTime(CMTime.Zero()));

        AVPlayerLayer layer = new AVPlayerLayer();
        layer.setPlayer(player);
        layer.setFrame(getView().getFrame());
        layer.setVideoGravity(AVLayerVideoGravity.ResizeAspect);

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
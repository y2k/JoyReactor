package y2k.joyreactor;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.images.ImageRequest;

/**
 * Created by y2k on 9/30/15.
 */
@CustomClass("ProfileViewController")
public class ProfileViewController extends UIViewController implements ProfilePresenter.View {

    UIImageView userImage;
    UILabel userName;
    UILabel rating;
    StarProgress starProgress;
    ProgressBar progressToNewStar;
    UIActivityIndicatorView busyIndicator;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new ProfilePresenter(this);
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    @Override
    public void setProfile(Profile profile) {
        userName.setText(profile.userName);
        new ImageRequest()
                .setUrl(profile.userImage)
                .setSize((int) userImage.getFrame().getWidth(), (int) userImage.getFrame().getHeight())
                .to(bitmap -> userImage.setImage(new UIImage(new NSData(bitmap))));
        rating.setText(Translator.get("Rating: ") + profile.rating);
        starProgress.setStars(profile.stars);
        progressToNewStar.setValue(profile.progressToNewStar);
    }

    @Override
    public void setProgress(boolean isProgress) {
        if (isProgress) busyIndicator.startAnimating();
        else busyIndicator.stopAnimating();
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setUserImage(UIImageView userImage) {
        this.userImage = userImage;
    }

    @IBOutlet
    void setUserName(UILabel userName) {
        this.userName = userName;
    }

    @IBOutlet
    void setRating(UILabel rating) {
        this.rating = rating;
    }

    @IBOutlet
    void setStarProgress(StarProgress starProgress) {
        this.starProgress = starProgress;
    }

    @IBOutlet
    void setProgressToNewStar(ProgressBar progressToNewStar) {
        this.progressToNewStar = progressToNewStar;
    }

    @IBOutlet
    void setBusyIndicator(UIActivityIndicatorView busyIndicator) {
        this.busyIndicator = busyIndicator;
    }
}
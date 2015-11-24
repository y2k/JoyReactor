package y2k.joyreactor;

import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.platform.StoryboardNavigation;
import y2k.joyreactor.presenters.PostListPresenter;

/**
 * Created by y2k on 10/11/15.
 */
@CustomClass("PostCell")
public class PostCell extends UITableViewCell {

    @IBOutlet
    UILabel replyCountView;

    @IBOutlet
    UILabel ratingView;

    @IBOutlet
    UIButton playButton;

    @IBOutlet
    UIButton rateButton;

    private PostListPresenter presenter;
    private Post post;

    @IBAction
    void rate() {
        UIAlertController alert = new UIAlertController();
        alert.addAction(new UIAlertAction(Translator.get("Like"), UIAlertActionStyle.Default, s -> {
            // TODO:
        }));
        alert.addAction(new UIAlertAction(Translator.get("Dislike"), UIAlertActionStyle.Destructive, s -> {
            // TODO:
        }));
        alert.addAction(new UIAlertAction(Translator.get("Cancel"), UIAlertActionStyle.Cancel, null));
        StoryboardNavigation.getNavigationController().presentViewController(alert, true, null);
    }

    @IBAction
    void clicked() {
        presenter.postClicked(post);
    }

    @IBAction
    void play() {
        presenter.playClicked(post);
    }

    public void update(PostListPresenter presenter, Post post) {
        this.presenter = presenter;
        this.post = post;

        replyCountView.setText("" + post.commentCount);
        ratingView.setText("" + post.rating);

        playButton.setHidden(post.image == null);
        playButton.setTitle(getPlayButtonTitle(), UIControlState.Normal);
    }

    private String getPlayButtonTitle() {
        return post.image != null && post.image.isAnimated()
                ? Translator.get("Play") : Translator.get("View");
    }
}
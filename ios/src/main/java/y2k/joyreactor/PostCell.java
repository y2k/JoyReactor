package y2k.joyreactor;

import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
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

    private PostListPresenter presenter;
    private Post post;

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

        playButton.setHidden(!post.isAnimated());
    }
}
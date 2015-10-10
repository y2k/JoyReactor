package y2k.joyreactor;

import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

/**
 * Created by y2k on 10/11/15.
 */
@CustomClass("PostCell")
public class PostCell extends UITableViewCell {

    private PostListPresenter presenter;
    private Post post;

    @IBAction
    void clicked() {
        presenter.postClicked(post);
    }

    public void update(PostListPresenter presenter, Post post) {
        this.presenter = presenter;
        this.post = post;
    }
}
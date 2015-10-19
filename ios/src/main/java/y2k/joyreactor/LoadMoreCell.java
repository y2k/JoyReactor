package y2k.joyreactor;

import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import y2k.joyreactor.presenters.PostListPresenter;

/**
 * Created by y2k on 05/10/15.
 */
@CustomClass("LoadMoreCell")
public class LoadMoreCell extends UITableViewCell {

    PostListPresenter presenter;

    @IBAction
    void clicked() {
        presenter.loadMore();
    }
}
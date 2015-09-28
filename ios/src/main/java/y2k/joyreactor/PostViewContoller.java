package y2k.joyreactor;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.images.ImageRequest;

/**
 * Created by y2k on 28/09/15.
 */

@CustomClass("PostViewContoller")
public class PostViewContoller extends UIViewController implements PostPresenter.View {

    CommentTableView.DataSource dataSource;
    UITableView list;

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        list.setDataSource(dataSource = new CommentTableView.DataSource());
        list.setDelegate(new CommentTableView.Delegate());
        new PostPresenter(this);
    }

    @Override
    public void updateComments(Comment.Collection comments) {
        dataSource.comments = comments;
        list.reloadData();
    }

    @Override
    public void updatePostImage(Post post) {
        dataSource.post = post;
        list.reloadData();
    }

    static class CommentTableView {

        static class DataSource extends UITableViewDataSourceAdapter {

            Comment.Collection comments;
            Post post;

            @Override
            public long getNumberOfRowsInSection(UITableView tableView, long section) {
                return (comments == null ? 0 : comments.size()) + 1;
            }

            @Override
            public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
                return indexPath.getRow() == 0
                        ? createHeaderCell(tableView)
                        : createCommentCell(tableView, indexPath);
            }

            UITableViewCell createHeaderCell(UITableView tableView) {
                UITableViewCell cell;
                cell = tableView.dequeueReusableCell("Header");
                if (post != null) {
                    UIImageView iv = (UIImageView) cell.getViewWithTag(1);
                    new ImageRequest()
                            .setUrl(post.image)
                            .setSize(300, (int) (300 / post.getAspect()))
                            .load(data -> iv.setImage(new UIImage(new NSData(data))));
                }
                return cell;
            }

            UITableViewCell createCommentCell(UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell;
                cell = tableView.dequeueReusableCell("Comment");
                Comment item = comments.get(indexPath.getRow() - 1);
                ((UILabel) cell.getViewWithTag(1)).setText(item.text);
                return cell;
            }
        }

        static class Delegate extends UITableViewDelegateAdapter {

            @Override
            public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
                return indexPath.getRow() == 0 ? 250 : -1;
            }
        }
    }
}

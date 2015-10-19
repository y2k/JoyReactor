package y2k.joyreactor;

import org.ocpsoft.prettytime.PrettyTime;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.platform.ImageRequest;
import y2k.joyreactor.presenters.PostListPresenter;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("PostListViewController")
public class PostListViewController extends UIViewController implements PostListPresenter.View {

    UITableView list;
    UIActivityIndicatorView progress;
    UIRefreshControl refresher;

    PostListPresenter presenter;
    List<Post> posts;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        list.setDataSource(new PostDataSource());
        list.setDelegate(new PostDelegate());

        new SideMenu(this, "Menu").attach();

        list.addSubview(refresher = new UIRefreshControl());

        getNavigationItem().getRightBarButtonItem().setOnClickListener(sender -> {
            UIAlertController alert = new UIAlertController();
            alert.addAction(new UIAlertAction("Add tag", UIAlertActionStyle.Default, s -> {
                getNavigationController().pushViewController(
                        getStoryboard().instantiateViewController("AddTag"), true);
            }));
            alert.addAction(new UIAlertAction("Profile", UIAlertActionStyle.Default, s -> {
                getNavigationController().pushViewController(
                        getStoryboard().instantiateViewController("Profile"), true);
            }));
            alert.addAction(new UIAlertAction("Messages", UIAlertActionStyle.Default, s -> {
                getNavigationController().pushViewController(
                        getStoryboard().instantiateViewController("MessageThreads"), true);
            }));
            alert.addAction(new UIAlertAction("Cancel", UIAlertActionStyle.Cancel, null));
            presentViewController(alert, true, null);
        });

        progress.stopAnimating();

        presenter = new PostListPresenter(this);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        presenter.activate();
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);
        presenter.deactivate();
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    @Override
    public void setBusy(boolean isBusy) {
        if (isBusy) refresher.beginRefreshing();
        else refresher.endRefreshing();
    }

    @Override
    public void reloadPosts(List<Post> posts) {
        this.posts = posts;
        list.reloadData();
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }

    @IBOutlet
    void setProgressView(UIActivityIndicatorView progress) {
        this.progress = progress;
    }

    class PostDataSource extends UITableViewDataSourceAdapter {

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            int count = posts == null ? 0 : posts.size();
            return count == 0 ? 0 : count + 1;
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            if (indexPath.getRow() == posts.size()) {
                LoadMoreCell cell = (LoadMoreCell) tableView.dequeueReusableCell("LoadMore");
                cell.presenter = presenter;
                return cell;
            } else {
                PostCell cell = (PostCell) tableView.dequeueReusableCell("Post");
                Post post = posts.get(indexPath.getRow());
                cell.update(presenter, post);

                ((UILabel) cell.getViewWithTag(3)).setText(post.userName);
                ((UILabel) cell.getViewWithTag(4)).setText(new PrettyTime().format(post.created));

                loadImage(post.image, 300, (int) (300 / post.getAspect()), (UIImageView) cell.getViewWithTag(1));

                UIImageView userImageView = (UIImageView) cell.getViewWithTag(2);
                loadImage(new UserImage(post.userImage).toString(), 50, 50, userImageView);
                userImageView.getLayer().setCornerRadius(userImageView.getFrame().getWidth() / 2);

                UIView root = cell.getViewWithTag(10);
                root.getLayer().setCornerRadius(10);
                root.getLayer().setMasksToBounds(true);
                root.getLayer().setBorderColor(UIColor.lightGray().getCGColor());
                root.getLayer().setBorderWidth(1);
                return cell;
            }
        }

        void loadImage(String url, int width, int height, UIImageView iv) {
            iv.setAlpha(0);
            new ImageRequest()
                    .setUrl(url)
                    .setSize(width, height)
                    .to(iv, data -> {
                        iv.setImage(data);
                        UIView.animate(0.3, () -> iv.setAlpha(1));
                    });
        }
    }

    class PostDelegate extends UITableViewDelegateAdapter {

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            if (indexPath.getRow() == posts.size()) return -1;
            Post post = posts.get(indexPath.getRow());
            return (tableView.getFrame().getWidth() - 16) / post.getAspect() + 66 + 16;
        }

        @Override
        public void didEndDecelerating(UIScrollView scrollView) {
            if (refresher.isRefreshing()) presenter.reloadFirstPage();
        }
    }
}
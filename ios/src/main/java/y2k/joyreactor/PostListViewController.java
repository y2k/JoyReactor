package y2k.joyreactor;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("PostListViewController")
public class PostListViewController extends UIViewController implements PostListPresenter.View {

    PostListPresenter presenter;
    PostDataSource dataSource;
    PostDelegate postDelegate;

    UITableView list;

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }

    UIActivityIndicatorView progress;

    @IBOutlet
    void setProgressView(UIActivityIndicatorView progress) {
        this.progress = progress;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        presenter = new PostListPresenter(this);
        list.setDataSource(dataSource = new PostDataSource());
        list.setDelegate(postDelegate = new PostDelegate());

        new SideMenu(this, "Menu").attach();

        getNavigationItem().getRightBarButtonItem().setOnClickListener(sender -> {
            UIActionSheet menu = new UIActionSheet();
            menu.addButton("Settings");
            menu.addButton("Profile");
            menu.addButton("Logout");
            menu.setCancelButtonIndex(menu.addButton("Cancel"));
            menu.showFrom(sender, true);
        });
    }

    @Override
    public void setBusy(boolean isBusy) {
        if (isBusy) progress.startAnimating();
        else progress.stopAnimating();
    }

    @Override
    public void reloadPosts(Post.Collection posts) {
        dataSource.posts = posts;
        postDelegate.posts = posts;
        list.reloadData();
    }

    class PostDataSource extends UITableViewDataSourceAdapter {

        Post.Collection posts;

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return posts == null ? 0 : posts.size();
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            UITableViewCell cell = tableView.dequeueReusableCell("Post");
            Post i = posts.get(indexPath.getRow());

            ((UILabel) cell.getViewWithTag(2)).setText(i.title);
            UIImageView iv = (UIImageView) cell.getViewWithTag(1);
            new ImageRequest().setUrl(i.image).load(data -> iv.setImage(new UIImage(new NSData(data))));

            UIView root = cell.getViewWithTag(3);
            root.getLayer().setCornerRadius(10);
            root.getLayer().setMasksToBounds(true);

            return cell;
        }
    }

    class PostDelegate extends UITableViewDelegateAdapter {

        Post.Collection posts;

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            Post post = posts.get(indexPath.getRow());
            if (post.height <= 0) return tableView.getFrame().getWidth();

            float aspect = (float) post.width / post.height;
            return 30 + tableView.getFrame().getWidth() / aspect;
        }
    }
}

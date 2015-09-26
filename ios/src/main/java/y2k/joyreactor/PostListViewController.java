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
//        ((UICollectionViewFlowLayout) list.getCollectionViewLayout()).setItemSize(new CGSize(150, 150 + 40));
    }

    @Override
    public void setBusy(boolean isBusy) {
        if (isBusy) progress.startAnimating();
        else progress.stopAnimating();
    }

    @Override
    public void reloadPosts(PostLoader.PostCollection posts) {
        dataSource.posts = posts;
        list.reloadData();
        System.out.println("reloadPosts | " + posts.size());
    }

    class PostDataSource extends UITableViewDataSourceAdapter {

        PostLoader.PostCollection posts;

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return posts == null ? 0 : posts.size();
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            UITableViewCell cell = tableView.dequeueReusableCell("Post");
            PostLoader.Post i = posts.get(indexPath.getRow());

            ((UILabel) cell.getViewWithTag(2)).setText(i.title);
            UIImageView iv = (UIImageView) cell.getViewWithTag(1);
            new ImageRequest().setUrl(i.image).load(data -> iv.setImage(new UIImage(new NSData(data))));

            return  cell;
        }
    }
}

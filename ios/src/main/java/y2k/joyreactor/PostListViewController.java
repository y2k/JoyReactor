package y2k.joyreactor;

import org.ocpsoft.prettytime.PrettyTime;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.images.ImageRequest;

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
            menu.addButton(Translator.get("Profile"));
            menu.addButton(Translator.get("Messages"));
            menu.setCancelButtonIndex(menu.addButton(Translator.get("Cancel")));
            menu.setDelegate(new UIActionSheetDelegateAdapter() {

                @Override
                public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                    if (buttonIndex == 0)
                        getNavigationController().pushViewController(
                                getStoryboard().instantiateViewController("Profile"), true);
                    else if (buttonIndex == 1)
                        getNavigationController().pushViewController(
                                getStoryboard().instantiateViewController("MessageThreads"), true);
                }
            });
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

            ((UILabel) cell.getViewWithTag(3)).setText(i.userName);
            ((UILabel) cell.getViewWithTag(4)).setText(new PrettyTime().format(i.created));

            loadImage(i.image, 300, (int) (300 / i.getAspect()), (UIImageView) cell.getViewWithTag(1));

            UIImageView userImageView = (UIImageView) cell.getViewWithTag(2);
            loadImage(i.userImage, 50, 50, userImageView);
            userImageView.getLayer().setCornerRadius(userImageView.getFrame().getWidth() / 2);

            UIView root = cell.getViewWithTag(10);
            root.getLayer().setCornerRadius(10);
            root.getLayer().setMasksToBounds(true);
            root.getLayer().setBorderColor(UIColor.lightGray().getCGColor());
            root.getLayer().setBorderWidth(1);

            return cell;
        }

        void loadImage(String url, int width, int height, UIImageView iv) {
            iv.setAlpha(0);
            new ImageRequest().setUrl(url)
                    .setSize(width, height)
                    .to(data -> {
                        iv.setImage(new UIImage(new NSData(data)));
                        UIView.animate(0.3, () -> iv.setAlpha(1));
                    });
        }
    }

    class PostDelegate extends UITableViewDelegateAdapter {

        Post.Collection posts;

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            Post post = posts.get(indexPath.getRow());
            return (tableView.getFrame().getWidth() - 16) / post.getAspect() + 66 + 16;
        }

        @Override
        public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
            getNavigationController().pushViewController(
                    getStoryboard().instantiateViewController("Post"), true);
        }
    }
}

package y2k.joyreactor;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.platform.ImageRequest;
import y2k.joyreactor.presenters.PostPresenter;

import java.io.File;
import java.util.List;

/**
 * Created by y2k on 28/09/15.
 */
@CustomClass("PostViewController")
public class PostViewController extends UIViewController implements PostPresenter.View {

    UITableView list;

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }

    PostPresenter presenter;

    CommentGroup comments;
    Post post;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

//        UILongPressGestureRecognizer gr = new UILongPressGestureRecognizer();
//        gr.setMinimumPressDuration(0.5);
//        gr.addListener(s -> {
//            if (s.getState() != UIGestureRecognizerState.Ended) return;
//
//            UIActionSheet menu = new UIActionSheet();
//            menu.addButton(Translator.get("Reply"));
//            menu.setCancelButtonIndex(menu.addButton(Translator.get("Cancel")));
//            menu.setDelegate(new UIActionSheetDelegateAdapter() {
//
//                @Override
//                public void clicked(UIActionSheet actionSheet, long buttonIndex) {
//                    if (buttonIndex == 0)
//                        getNavigationController().pushViewController(
//                                getStoryboard().instantiateViewController("CreateComment"), true);
//                }
//            });
//            menu.showFrom(getNavigationItem().getRightBarButtonItem(), true);
//        });
//        list.addGestureRecognizer(gr);

        getNavigationItem().getRightBarButtonItem().setOnClickListener(sender -> {
            UIAlertController alert = new UIAlertController();
            alert.addAction(new UIAlertAction(Translator.get("Add comment"), UIAlertActionStyle.Default, s -> {
                getNavigationController().pushViewController(
                        getStoryboard().instantiateViewController("CreateComment"), true);
            }));
            alert.addAction(new UIAlertAction(Translator.get("Save image to gallery"),
                    UIAlertActionStyle.Default, s -> presenter.saveImageToGallery()));
            alert.addAction(new UIAlertAction(Translator.get("Open in Safari"),
                    UIAlertActionStyle.Default, s -> presenter.openPostInBrowser()));
            alert.addAction(new UIAlertAction("Cancel", UIAlertActionStyle.Cancel, null));
            presentViewController(alert, true, null);
        });

        list.setDelegate(new CommentDelegate());
        list.setDataSource(new CommentDataSource());
        list.setRowHeight(UITableView.getAutomaticDimension());
        list.setEstimatedRowHeight(44);

        ServiceLocator.getInstance().providePostPresenter(this);
    }

    @Override
    public void updateComments(CommentGroup comments) {
        this.comments = comments;
        list.reloadData();
    }

    @Override
    public void updatePostInformation(Post post) {
        this.post = post;
        list.reloadData();
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        getNavigationItem().setHidesBackButton(isBusy, true);
        getNavigationItem().getRightBarButtonItem().setEnabled(!isBusy);
    }

    @Override
    public void showImageSuccessSavedToGallery() {
        // TODO
    }

    @Override
    public void updatePostImages(List<? extends Image> images) {
        // TODO
    }

    @Override
    public void updateSimilarPosts(List<? extends SimilarPost> similarPosts) {
        // TODO
    }

    @Override
    public void updatePostImage(File image) {
        // TODO:
    }

    @Override
    public void updateImageDownloadProgress(int progress, int maxProgress) {
        // TODO:
    }

    private class CommentDataSource extends UITableViewDataSourceAdapter {

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

                Image image = post.getImage();
                if (image == null) {
                    // TODO:
                } else {
                    new ImageRequest()
                            .setUrl(post.getImage())
                            .setSize(300, (int) (300 / image.getAspect()))
                            .to(iv, iv::setImage);
                }
            }
            return cell;
        }

        UITableViewCell createCommentCell(UITableView tableView, NSIndexPath indexPath) {
            UITableViewCell cell;
            cell = tableView.dequeueReusableCell("Comment");
            Comment item = comments.get(indexPath.getRow() - 1);
            ((UILabel) cell.getViewWithTag(1)).setText(item.text);

            UIImageView iv = (UIImageView) cell.getViewWithTag(2);
            iv.getLayer().setCornerRadius(iv.getFrame().getWidth() / 2);
            new ImageRequest()
                    .setUrl(item.getUserImage().toImage())
                    .setSize((int) iv.getFrame().getWidth(), (int) iv.getFrame().getHeight())
                    .to(iv, iv::setImage);

            ((UILabel) cell.getViewWithTag(3)).setText("" + item.replies);
            ((UILabel) cell.getViewWithTag(4)).setText("" + item.rating);

            return cell;
        }
    }

    private class CommentDelegate extends UITableViewDelegateAdapter {

        @Override
        public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
            presenter.selectComment(comments.get(indexPath.getRow() - 1).id);
            tableView.deselectRow(indexPath, true);
        }
    }
}
package y2k.joyreactor;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("MenuViewController")
public class MenuViewController extends UIViewController implements MenuPresenter.View {

    private MenuPresenter presenter;

    UITableView list;

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        presenter = new MenuPresenter(this);
        list.setDataSource(new TagDataSource());
        list.setDelegate(new TagDelegate());
    }

    @Override
    public void reloadData() {
        list.reloadData();
    }

    class TagDataSource extends UITableViewDataSourceAdapter {

        @Override
        public long getNumberOfRowsInSection(UITableView tableView, long section) {
            return presenter.tags.size() + 1;
        }

        @Override
        public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
            UITableViewCell cell;
            if (indexPath.getRow() == 0) {
                cell = tableView.dequeueReusableCell("Header");
            } else {
                cell = tableView.dequeueReusableCell("Tag");
                Tag i = presenter.tags.get(indexPath.getRow() - 1);

                UIImageView iv = (UIImageView) cell.getViewWithTag(1);
                iv.getLayer().setCornerRadius(iv.getFrame().getWidth() / 2);
                new ImageRequest()
                        .setUrl(i.image)
                        .load(data -> iv.setImage(new UIImage(new NSData(data))));
                ((UILabel) cell.getViewWithTag(2)).setText(i.title);
            }
            return cell;
        }
    }

    class TagDelegate extends UITableViewDelegateAdapter {

        @Override
        public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
            return indexPath.getRow() == 0 ? 136 : 50;
        }
    }
}
package y2k.joyreactor;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

import java.util.List;

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageThreadViewController")
public class MessageThreadViewController extends UIViewController implements MessageThreadsPresenter.View {

    UITableView list;
    List<MessageThread> threads;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new MessageThreadsPresenter(this);
        list.setDataSource(new UITableViewDataSourceAdapter() {

            @Override
            public long getNumberOfRowsInSection(UITableView tableView, long section) {
                return threads == null ? 0 : threads.size();
            }

            @Override
            public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell = tableView.dequeueReusableCell("Thread");
                MessageThread thread = threads.get(indexPath.getRow());
                cell.getTextLabel().setText(thread.lastMessage);
                return cell;
            }
        });
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        // TODO:
    }

    @Override
    public void reloadData(List<MessageThread> threads) {
        this.threads = threads;
        list.reloadData();
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }
}
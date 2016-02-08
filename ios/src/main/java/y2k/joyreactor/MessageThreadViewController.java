package y2k.joyreactor;

import org.jetbrains.annotations.NotNull;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.MessageThreadsPresenter;

import java.util.List;

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageThreadViewController")
public class MessageThreadViewController extends UIViewController {

    UITableView list;
    List<Message> threads;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        list.setDataSource(new UITableViewDataSourceAdapter() {

            @Override
            public long getNumberOfRowsInSection(UITableView tableView, long section) {
                return threads == null ? 0 : threads.size();
            }

            @Override
            public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell = tableView.dequeueReusableCell("Thread");
                Message thread = threads.get(indexPath.getRow());
                cell.getTextLabel().setText(thread.getText());
                return cell;
            }
        });
        list.setDelegate(new UITableViewDelegateAdapter() {

            @Override
            public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
                UIViewController vc = getStoryboard().instantiateViewController("Messages");
                getNavigationController().pushViewController(vc, true);
            }
        });

        ServiceLocator.INSTANCE.resolve(new MessageThreadsPresenter.View() {

            @Override
            public void reloadData(@NotNull List<Message> threads) {
                MessageThreadViewController.this.threads = threads;
                list.reloadData();
            }

            @Override
            public void setIsBusy(boolean isBusy) {
                // TODO:
            }
        });
    }

    // ==========================================
    // Outlets
    // ==========================================

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }
}
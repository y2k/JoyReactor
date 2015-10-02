package y2k.joyreactor;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

import java.util.List;

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageViewController")
public class MessageViewController extends UIViewController implements MessagesPresenter.View {

    UITableView list;
    List<Message> messages;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        list.setDataSource(new UITableViewDataSourceAdapter() {

            @Override
            public long getNumberOfRowsInSection(UITableView tableView, long section) {
                return messages == null ? 0 : messages.size();
            }

            @Override
            public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell = tableView.dequeueReusableCell("Message");
                Message thread = messages.get(indexPath.getRow());
                cell.getTextLabel().setText(thread.text);
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

        new MessagesPresenter(this);
    }

    @Override
    public void updateMessages(List<Message> messages) {
        System.out.println("updateMessages | " + messages);
        this.messages = messages;
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
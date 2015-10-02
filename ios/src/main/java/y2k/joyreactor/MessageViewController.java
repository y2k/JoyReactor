package y2k.joyreactor;

import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

import java.util.List;

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageViewController")
public class MessageViewController extends UIViewController implements MessagesPresenter.View {

    UITableView list;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        new MessagesPresenter(this);
    }

    @Override
    public void updateMessages(List<Message> messages) {
        // TOOD:
    }

    @IBOutlet
    void setList(UITableView list) {
        this.list = list;
    }
}
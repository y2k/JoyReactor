package y2k.joyreactor;

import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

/**
 * Created by y2k on 05/10/15.
 */
@CustomClass("LoadMoreCell")
public class LoadMoreCell extends UITableViewCell {

    @IBAction
    void clicked() {
        new UIActionSheet("test", null, "cancel", null).showIn(null);
    }
}
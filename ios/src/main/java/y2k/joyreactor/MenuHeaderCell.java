package y2k.joyreactor;

import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import y2k.joyreactor.presenters.TagsPresenter;

/**
 * Created by y2k on 20/10/15.
 */
@CustomClass("MenuHeaderCell")
public class MenuHeaderCell extends UITableViewCell {

    private TagsPresenter presenter;

    public void setPresenter(TagsPresenter presenter) {
        this.presenter = presenter;
    }

    @IBAction
    void featuredClicked() {
        presenter.selectedFeatured();
    }

    @IBAction
    void favoriteClicked() {
        presenter.selectedFavorite();
    }
}
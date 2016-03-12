//package y2k.joyreactor
//
//import org.robovm.apple.uikit.UITableViewCell
//import org.robovm.objc.annotation.CustomClass
//import org.robovm.objc.annotation.IBAction
//import y2k.joyreactor.presenters.TagListPresenter
//
///**
// * Created by y2k on 20/10/15.
// */
//@CustomClass("MenuHeaderCell")
//class MenuHeaderCell : UITableViewCell() {
//
//    private lateinit var presenter: TagListPresenter
//
//    fun setPresenter(presenter: TagListPresenter) {
//        this.presenter = presenter
//    }
//
//    @IBAction
//    internal fun featuredClicked() {
//        presenter.selectedFeatured()
//    }
//
//    @IBAction
//    internal fun favoriteClicked() {
//        presenter.selectedFavorite()
//    }
//}
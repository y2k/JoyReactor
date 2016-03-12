package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UIImageView
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.uikit.UITableView
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.BaseUIViewController
import y2k.joyreactor.common.ListCell
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.model.Tag
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.viewmodel.TagListViewModel

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("MenuViewController")
class MenuViewController : BaseUIViewController() {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var featuredButton: UIButton
    @IBOutlet lateinit var favoriteButton: UIButton

    //    var tags: List<Tag>? = null
    //    lateinit var presenter: TagListPresenter

    override fun viewDidLoad() {
        super.viewDidLoad()

        //        presenter = ServiceLocator.resolve(lifeCycleService,
        //            object : TagListPresenter.View {
        //
        //                override fun reloadData(tags: List<Tag>) {
        //                    this@MenuViewController.tags = tags
        //                    list.reloadData()
        //                }
        //            })
        //
        //        list.dataSource = TagDataSource()
        //        list.setDelegate(TagDelegate())

        val vm = ServiceLocator.resolve<TagListViewModel>(lifeCycleService)
        bindingBuilder {
            command(featuredButton) { vm.selectedFeatured() }
            command(favoriteButton) { vm.selectedFavorite() }
            tableView(list, vm.tags) {
                cellSelector { "Tag" }
                command { vm.selectTag(it) }
            }
        }
    }

    @CustomClass("TagCell")
    class TagCell : ListCell<Tag>() {

        @IBOutlet lateinit var icon: UIImageView
        @IBOutlet lateinit var title: UILabel

        override fun bind(data: Tag) {
            icon.layer.cornerRadius = icon.frame.width / 2
            ImageRequest()
                .setUrl(data.image)
                .setSize(40, 40)
                .to(icon, { icon.image = it })
            title.text = data.title
        }
    }

    //    inner class TagDataSource : UITableViewDataSourceAdapter() {
    //
    //        override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
    //            return ((if (tags == null) 0 else tags!!.size) + 1).toLong()
    //        }
    //
    //        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
    //            if (indexPath.row == 0) {
    //                val cell = tableView.dequeueReusableCell("Header") as MenuHeaderCell
    //                cell.setPresenter(presenter)
    //                return cell
    //            } else {
    //                val cell = tableView.dequeueReusableCell("Tag")
    //                val i = tags!![indexPath.row - 1]
    //
    //                val iv = cell.getViewWithTag(1) as UIImageView
    //                iv.layer.cornerRadius = iv.frame.width / 2
    //                ImageRequest()
    //                    .setUrl(i.image)
    //                    .setSize(40, 40)
    //                    .to(iv, { iv.image = it })
    //                (cell.getViewWithTag(2) as UILabel).text = i.title
    //                return cell
    //            }
    //        }
    //    }
    //
    //    inner class TagDelegate : UITableViewDelegateAdapter() {
    //
    //        override fun getHeightForRow(tableView: UITableView, indexPath: NSIndexPath): Double {
    //            return (if (indexPath.row == 0) 136 else 50).toDouble()
    //        }
    //
    //        override fun didSelectRow(tableView: UITableView, indexPath: NSIndexPath) {
    //            presenter.selectTag(tags!![indexPath.row - 1])
    //            tableView.deselectRow(indexPath, true)
    //        }
    //    }
}
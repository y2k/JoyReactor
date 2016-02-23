package y2k.joyreactor

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.model.Message
import y2k.joyreactor.presenters.MessageThreadsPresenter

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageThreadViewController")
class MessageThreadViewController : UIViewController() {

    @IBOutlet lateinit var list: UITableView
    var threads: List<Message> = emptyList()

    override fun viewDidLoad() {
        super.viewDidLoad()
        list.dataSource = object : UITableViewDataSourceAdapter() {

            override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
                return  threads.size.toLong()
            }

            override fun getCellForRow(tableView: UITableView?, indexPath: NSIndexPath?): UITableViewCell {
                val cell = tableView!!.dequeueReusableCell("Thread")
                val thread = threads[indexPath!!.row]
                cell.textLabel.text = thread.text
                return cell
            }
        }
        list.setDelegate(object : UITableViewDelegateAdapter() {

            override fun didSelectRow(tableView: UITableView?, indexPath: NSIndexPath?) {
                val vc = storyboard.instantiateViewController("Messages")
                navigationController.pushViewController(vc, true)
            }
        })

        ServiceLocator.resolve(object : MessageThreadsPresenter.View {

            override fun reloadData(threads: List<Message>) {
                this@MessageThreadViewController.threads = threads
                list.reloadData()
            }

            override fun setIsBusy(isBusy: Boolean) {
                // TODO:
            }
        })
    }
}
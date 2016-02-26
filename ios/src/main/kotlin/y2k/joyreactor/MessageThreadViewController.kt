package y2k.joyreactor

import org.ocpsoft.prettytime.PrettyTime
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UITableViewDelegateAdapter
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.BaseUIViewController
import y2k.joyreactor.common.ListCell
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.model.Message
import y2k.joyreactor.viewmodel.ThreadsViewModel

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageThreadViewController")
class MessageThreadViewController : BaseUIViewController() {

    @IBOutlet lateinit var list: UITableView

    override fun viewDidLoad() {
        super.viewDidLoad()

        val vm = ServiceLocator.resolve(lifeCycleService, ThreadsViewModel::class)
        bindingBuilder {
            tableView(list, ThreadCell::class, vm.threads)
        }

        // TODO:
        list.setDelegate(object : UITableViewDelegateAdapter() {

            override fun didSelectRow(tableView: UITableView?, indexPath: NSIndexPath?) {
                val vc = storyboard.instantiateViewController("Messages")
                navigationController.pushViewController(vc, true)
            }
        })
    }

    @CustomClass("ThreadCell")
    class ThreadCell : ListCell<Message>() {

        val prettyTime = PrettyTime()

        override fun bind(data: Message) {
            textLabel.text = data.text
            detailTextLabel.text = prettyTime.format(data.date)
        }
    }
}
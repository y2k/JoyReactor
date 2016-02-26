package y2k.joyreactor

import org.ocpsoft.prettytime.PrettyTime
import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UITextView
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.BaseUIViewController
import y2k.joyreactor.common.ListCell
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.model.Message
import y2k.joyreactor.viewmodel.MessagesViewModel

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageViewController")
class MessagesViewController : BaseUIViewController() {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var newMessage: UITextView
    @IBOutlet lateinit var sendButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        // TODO:
        //        list.setDelegate(object : UITableViewDelegateAdapter() {
        //
        //            override fun didSelectRow(tableView: UITableView?, indexPath: NSIndexPath?) {
        //                val vc = storyboard.instantiateViewController("Messages")
        //                navigationController.pushViewController(vc, true)
        //            }
        //        })

        val vm = ServiceLocator.resolve(lifeCycleService, MessagesViewModel::class)
        bindingBuilder {
            textView(newMessage, vm.newMessage)
            click(sendButton, { vm.sendNewMessage() })
            tableView(list, MessageCell::class, vm.messages)
        }
    }

    @CustomClass("MessageCell")
    class MessageCell : ListCell<Message>() {

        val prettyTime = PrettyTime()

        override fun bind(data: Message) {
            textLabel.text = data.text
            detailTextLabel.text = prettyTime.format(data.date)
        }
    }
}
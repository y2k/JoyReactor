package y2k.joyreactor

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.BaseUIViewController
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.model.Message
import y2k.joyreactor.presenters.MessagesPresenter

/**
 * Created by y2k on 10/2/15.
 */
@CustomClass("MessageViewController")
class MessageViewController : BaseUIViewController(), MessagesPresenter.View {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var newMessage: UITextView
    @IBOutlet lateinit var sendButton: UIButton

    internal var messages: List<Message> = emptyList()
    lateinit var presenter: MessagesPresenter

    override fun viewDidLoad() {
        super.viewDidLoad()
        list.dataSource = object : UITableViewDataSourceAdapter() {

            override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
                return  messages.size.toLong()
            }

            override fun getCellForRow(tableView: UITableView?, indexPath: NSIndexPath?): UITableViewCell {
                val cell = tableView!!.dequeueReusableCell("Message")
                val thread = messages[indexPath!!.row]
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
        sendButton.addOnTouchUpInsideListener { sender, e -> presenter.reply(newMessage.text) }

        presenter = ServiceLocator.resolve(lifeCycleService, this)
    }

    override fun updateMessages(messages: List<Message>) {
        println("updateMessages | " + messages)
        this.messages = messages
        list.reloadData()
    }

    override fun setBusy(isBusy: Boolean) {
        // TODO:
        navigationItem.setHidesBackButton(isBusy, true)
    }

    override fun clearMessage() {
        newMessage.text = null
    }
}
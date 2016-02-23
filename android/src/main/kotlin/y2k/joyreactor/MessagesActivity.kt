package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ListViewHolder
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.inflate
import y2k.joyreactor.model.Message
import y2k.joyreactor.viewmodel.MessagesViewModel

/**
 * Created by y2k on 11/13/15.
 */
class MessagesActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        val vm = ServiceLocator.resolve(MessagesViewModel::class)
        bindingBuilder(this) {
            editText(R.id.newMessage, vm.newMessage)
            click(R.id.createMessage, { vm.sendNewMessage() })
            recyclerView(R.id.list, vm.messages) {
                itemViewType {
                    if (it.value.isMine) {
                        if (isFirst(it.items, it.position)) R.layout.item_message_outbox_first
                        else R.layout.item_message_outbox
                    } else {
                        if (isFirst(it.items, it.position)) R.layout.item_message_inbox_first
                        else R.layout.item_message_inbox
                    }
                }
                viewHolderWithType { parent, type -> VH(parent.inflate(type)) }
            }
        }
    }

    private fun isFirst(items: List<Message>, position: Int): Boolean {
        return position == items.size - 1 || items[position].isMine != items[position + 1].isMine
    }

    class VH(view: View) : ListViewHolder<Message>(view) {

        val message = view.findViewById(R.id.message) as TextView
        val created = view.findViewById(R.id.created) as TextView
        val prettyTime = PrettyTime()

        override fun update(item: Message) {
            message.text = item.text
            created.text = prettyTime.format(item.date)
        }
    }
}
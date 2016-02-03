package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.MessagesPresenter

/**
 * Created by y2k on 11/20/15.
 */
class MessageFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        val newMessage = view.findViewById(R.id.newMessage) as EditText
        val list = view.findViewById(R.id.list) as RecyclerView
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        list.adapter = MessageAdapter()

        val presenter = ServiceLocator.resolve(lifeCycleService, object : MessagesPresenter.View {

            override fun updateMessages(messages: List<Message>) {
                (list.adapter as MessageAdapter).update(messages)
            }

            override fun setBusy(isBusy: Boolean) {
                // TODO:
            }
        })

        view.findViewById(R.id.createMessage).setOnClickListener {
            presenter.reply("" + newMessage.text)
        }
        return view
    }

    class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

        private var items: List<Message> = emptyList()
        private val prettyTime = PrettyTime()

        fun update(items: List<Message>) {
            this.items = items
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
            return MessageHolder(LayoutInflater.from(
                parent.context).inflate(viewType, parent, false))
        }

        override fun onBindViewHolder(holder: MessageHolder, position: Int) {
            val i = items[position]
            holder.message.text = i.text
            holder.created.text = prettyTime.format(i.date)
        }

        override fun getItemViewType(position: Int): Int {
            if (items[position].isMine)
                return if (isFirst(position))
                    R.layout.item_message_outbox_first
                else
                    R.layout.item_message_outbox
            return if (isFirst(position))
                R.layout.item_message_inbox_first
            else
                R.layout.item_message_inbox
        }

        private fun isFirst(position: Int): Boolean {
            return position == items.size - 1 || items[position].isMine != items[position + 1].isMine
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {

        var message: TextView
        var created: TextView

        init {
            message = view.findViewById(R.id.message) as TextView
            created = view.findViewById(R.id.created) as TextView
        }
    }
}
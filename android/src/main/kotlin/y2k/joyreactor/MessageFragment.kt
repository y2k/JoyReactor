package y2k.joyreactor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.MessagesPresenter

/**
 * Created by y2k on 11/20/15.
 */
class MessageFragment : Fragment(), MessagesPresenter.View {

    private var adapter: MessageAdapter? = null
    private var presenter: MessagesPresenter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_messages, container, false)

        val list = view.findViewById(R.id.list) as RecyclerView
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        list.adapter = MessageAdapter()

        val newMessage = view.findViewById(R.id.newMessage) as EditText
        view.findViewById(R.id.createMessage).setOnClickListener { v -> presenter!!.reply("" + newMessage.text) }

        presenter = ServiceLocator.provideMessagesPresenter(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter!!.activate()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.deactivate()
    }

    override fun updateMessages(messages: List<Message>) {
        adapter!!.update(messages)
    }

    override fun setIsBusy(isBusy: Boolean) {
        // TODO:
    }

    internal class MessageAdapter : RecyclerView.Adapter<ViewHolderImpl>() {

        private var items: List<Message>? = null

        fun update(items: List<Message>) {
            this.items = items
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImpl {
            return ViewHolderImpl(LayoutInflater.from(
                parent.context).inflate(viewType, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) {
            val i = items!![position]
            holder.message.text = i.text
            holder.created.text = PrettyTime().format(i.date)
        }

        override fun getItemViewType(position: Int): Int {
            if (items!![position].isMine)
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
            return position == items!!.size - 1 || items!![position].isMine != items!![position + 1].isMine
        }

        override fun getItemCount(): Int {
            return if (items == null) 0 else items!!.size
        }
    }

    internal class ViewHolderImpl(view: View) : RecyclerView.ViewHolder(view) {

        var message: TextView
        var created: TextView

        init {
            message = view.findViewById(R.id.message) as TextView
            created = view.findViewById(R.id.created) as TextView
        }
    }
}
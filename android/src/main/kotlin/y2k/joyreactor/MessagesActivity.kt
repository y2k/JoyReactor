package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Message
import y2k.joyreactor.viewmodel.ThreadsViewModel

/**
 * Created by y2k on 11/13/15.
 */
class MessagesActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        val vm = ServiceLocator.resolve(ThreadsViewModel::class)
        bindingBuilder(this) {
            loadingProgressBar(R.id.progress, vm.isBusy)
            visibility(R.id.progress, vm.isBusy)
            recyclerView(R.id.list, vm.threads) {
                viewHolder {
                    VH(it.inflate(R.layout.item_message_thread)).apply {
                        itemView.findViewById(R.id.button).setOnClickListener {
                            vm.selectThread(adapterPosition)
                        }
                    }
                }
            }
        }
    }

    class VH(view: View) : ListViewHolder<Message>(view) {

        val userImage = view.find<WebImageView>(R.id.userImage)
        val userName = view.find<TextView>(R.id.userName)
        val lastMessage = view.find<TextView>(R.id.lastMessage)
        val time = view.find<TextView>(R.id.time)
        val prettyTime = PrettyTime()

        override fun update(item: Message) {
            userImage.setImage(item.getUserImageObject().toImage())
            userName.text = item.userName
            lastMessage.text = item.text
            time.text = prettyTime.format(item.date)
        }
    }
}
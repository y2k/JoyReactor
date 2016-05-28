package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Message
import y2k.joyreactor.viewmodel.ThreadsViewModel
import y2k.joyreactor.widget.WebImageView

/**
 * Created by y2k on 2/23/16.
 */
class ThreadsActivity : LifeCycleActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_threads)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        val vm = ServiceLocator.resolve<ThreadsViewModel>(lifeCycleService)
        bindingBuilder(this) {
            refreshLayout(R.id.refresher) {
                isRefreshing(vm.isBusy)
                command { vm.refresh() }
            }
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
            userImage.image = item.getUserImageObject().toImage()
            userName.text = item.userName
            lastMessage.text = item.text
            time.text = prettyTime.format(item.date)
        }
    }
}
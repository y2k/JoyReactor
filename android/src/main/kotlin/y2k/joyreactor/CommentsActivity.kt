package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.Toolbar
import y2k.joyreactor.common.*
import y2k.joyreactor.viewmodel.CommentsViewModel

/**
 * Created by y2k on 5/30/16.
 */
class CommentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val vm = ServiceLocator.resolve<CommentsViewModel>(lifeCycleService)
        bindingBuilder(this) {
            visibility(R.id.createComment, vm.canCreateComments)
            command(R.id.createComment) { vm.commentPost() }

            recyclerView(R.id.list, vm.comments) {
                itemId { it.id }
                viewHolder {
                    PostActivity.CommentViewHolder(it).apply {
                        setOnClick(R.id.action) { vm.selectComment(it) }
                    }
                }
            }
        }
    }
}
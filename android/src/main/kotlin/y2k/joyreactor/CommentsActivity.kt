package y2k.joyreactor

import android.os.Bundle
import y2k.joyreactor.common.BaseActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.setOnClick
import y2k.joyreactor.viewmodel.CommentsViewModel
import y2k.joyreactor.widget.CommentComponent

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
                component {
                    CommentComponent(it.context).apply {
                        setOnClick(R.id.action) { vm.selectComment(value.value) }
                    }
                }
            }
        }
    }
}
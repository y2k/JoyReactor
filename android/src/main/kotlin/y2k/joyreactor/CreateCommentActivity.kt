package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.CreateCommentViewModel

class CreateCommentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comment)

        val vm = ServiceLocator.resolve<CreateCommentViewModel>()
        bindingBuilder(this) {
            webImageView(R.id.userImage, vm.avatar)
            textView(R.id.userName, vm.username)
            editText(R.id.text, vm.commentText)
            command(R.id.send, { vm.create() })
        }
    }
}
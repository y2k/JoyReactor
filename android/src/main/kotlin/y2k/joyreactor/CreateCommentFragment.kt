package y2k.joyreactor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.LifeCycleBottomSheetDialogFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.CreateCommentViewModel

/**
* Created by y2k on 5/22/2016.
*/
class CreateCommentFragment : LifeCycleBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_create_comment, container, false)
        val vm = ServiceLocator.resolve<CreateCommentViewModel>(lifeCycle)
        bindingBuilder(view) {
            webImageView(R.id.userImage, vm.avatar)
            textView(R.id.userName, vm.username)
            editText(R.id.text, vm.commentText)
            command(R.id.send) { vm.create() }
        }
        return view
    }
}
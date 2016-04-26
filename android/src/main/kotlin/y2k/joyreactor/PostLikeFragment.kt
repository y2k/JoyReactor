package y2k.joyreactor

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.BaseDialogFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.PostLikeViewModel

/**
 * Created by y2k on 4/25/16.
 */
class PostLikeFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_like_post, container, false)
        val vm = ServiceLocator.resolve<PostLikeViewModel>(lifeCycleService)
        bindingBuilder(view) {
            blockDialog(dialog, vm.isBusy)
            animator(R.id.animator, vm.isBusy)
            visibility(R.id.error, vm.isError)
            command(R.id.like) { vm.like() }
            command(R.id.dislike) { vm.dislike() }
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.AppTheme_Dialog).apply {
            setTitle(R.string.app_name)
        }
    }
}
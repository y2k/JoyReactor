package y2k.joyreactor

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import y2k.joyreactor.common.BaseDialogFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.viewmodel.PostLikeViewModel

/**
 * Created by y2k on 4/25/16.
 */
class PostLikeFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ServiceLocator.resolve<PostLikeViewModel>(lifeCycleService)
        return AlertDialog
            .Builder(activity)
            .setTitle(R.string.app_name)
            .setMessage("Like post")
            .setPositiveButton("Like") { dialog, which -> vm.like() }
            .setNegativeButton("Dislike") { dialog, which -> vm.dislike() }
            .create()
    }
}
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
import y2k.joyreactor.viewmodel.AddTagViewModel

/**
 * Created by y2k on 11/27/15.
 */
class AddTagDialogFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_add_tag, container, false)
        val vm = ServiceLocator.resolve<AddTagViewModel>()
        bindingBuilder(view) {
            command(R.id.cancel, { dismiss() })
            command(R.id.ok, { vm.add() })
            editText(R.id.tag, vm.tag)
            visibility(R.id.error, vm.error)
            animator(R.id.animator, vm.isBusy, { if (it) 1 else 0 })
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.AppTheme_Dialog).apply {
            setTitle(R.string.add_tag)
        }
    }
}
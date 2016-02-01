package y2k.joyreactor

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.AddTagPresenter

/**
 * Created by y2k on 11/27/15.
 */
class AddTagDialogFragment : AppCompatDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_add_tag, container, false)

        val cancelButton = view.findViewById(R.id.cancel)
        val okButton = view.findViewById(R.id.ok)
        val tagView = view.findViewById(R.id.tag) as TextView

        val presenter = ServiceLocator.provideAddTagPresenter(
            object : AddTagPresenter.View {

                override fun setIsBusy(isBusy: Boolean) {
                    okButton.isEnabled = !isBusy
                    cancelButton.isEnabled = !isBusy
                    tagView.isEnabled = !isBusy
                    isCancelable = !isBusy
                }

                override fun showErrorMessage() {
                    Toast.makeText(App.instance, R.string.unknown_error_occurred, Toast.LENGTH_LONG).show()
                }
            })

        cancelButton.setOnClickListener { v -> dismiss() }
        okButton.setOnClickListener { v -> presenter.add("" + tagView.text) }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.add_tag)
        return dialog
    }

    companion object {

        private val TAG_ID = "add_tag"

        fun show(fragmentManager: FragmentManager) {
            AddTagDialogFragment().show(fragmentManager, TAG_ID)
        }

        fun dismiss(activity: AppCompatActivity) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG_ID) as AddTagDialogFragment
            dialog.dismiss()
        }
    }
}
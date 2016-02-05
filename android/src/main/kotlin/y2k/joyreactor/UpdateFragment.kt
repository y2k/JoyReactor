package y2k.joyreactor

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.UpdateService

/**
 * Created by y2k on 05/02/16.
 */
class UpdateFragment : BaseFragment() {

    lateinit var service: UpdateService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        service = UpdateService(activity.applicationContext)
        return Button(activity).apply {
            setText(R.string.updates_available)
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)

            setOnClickListener {
                setBlocked(true)
                service
                    .update()
                    .subscribeOnMain({
                        setBlocked(false)
                    }, {
                        setBlocked(false)
                        Toast.makeText(activity, R.string.unknow_error, Toast.LENGTH_LONG).show()
                    })
            }
        }
    }

    private fun Button.setBlocked(blocked: Boolean) {
        isEnabled = !blocked; animate().alpha(if (blocked) 0.5f else 1f)
    }

    override fun onResume() {
        super.onResume()
        view.isVisible = false
        service.checkHasUpdates().subscribeOnMain { view.isVisible = it }
    }
}
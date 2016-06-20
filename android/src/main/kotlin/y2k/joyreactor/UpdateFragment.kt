package y2k.joyreactor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.common.ui
import y2k.joyreactor.common.switchByScaleFromTo
import y2k.joyreactor.platform.UpdateService

/**
 * Created by y2k on 05/02/16.
 */
class UpdateFragment : BaseFragment() {

    lateinit var service: UpdateService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        service = UpdateService(activity.applicationContext)
        val view = inflater.inflate(R.layout.fragment_update, container)
        view.findViewById(R.id.button)
            .setOnClickListener {
                setBlocked(true)
                service
                    .update()
                    .ui({
                        setBlocked(false)
                    }, {
                        setBlocked(false)
                        Toast.makeText(activity, R.string.unknow_error, Toast.LENGTH_LONG).show()
                    })
            }
        return view
    }

    private fun setBlocked(blocked: Boolean) {
        val group = view as ViewGroup
        if (blocked) group.switchByScaleFromTo(0, 1)
        else group.switchByScaleFromTo(1, 0)
    }

    override fun onResume() {
        super.onResume()
        view?.isVisible = false
        service.checkHasUpdates().ui { view?.isVisible = it }
    }
}
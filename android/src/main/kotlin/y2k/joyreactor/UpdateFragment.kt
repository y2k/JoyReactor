package y2k.joyreactor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.common.switchByScaleFromTo
import y2k.joyreactor.platform.UpdateService

/**
 * Created by y2k on 05/02/16.
 */
class UpdateFragment : BaseFragment() {

    val service = UpdateService(ServiceLocator.resolve(), App.instance, ServiceLocator.resolve())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_update, container)
        lifeCycleService(service.requestDownloadUpdate()) {
            setBlocked(service.isCheckInProgress())
            view.isVisible = service.hasFileToInstall()
        }
        view.findViewById(R.id.button).setOnClickListener { service.installUpdate() }
        return view
    }

    private fun setBlocked(blocked: Boolean) {
        val group = view as ViewGroup
        if (blocked) group.switchByScaleFromTo(0, 1)
        else group.switchByScaleFromTo(1, 0)
    }
}
package y2k.joyreactor

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.UpdateService

/**
 * Created by y2k on 05/02/16.
 */
class UpdateFragment : BaseFragment() {

    val service = ServiceLocator.resolve(UpdateService::class)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return Button(activity).apply {
            setText(R.string.updates_available)
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            this.isVisible = false

            setOnClickListener {
                isEnabled = false; animate().alpha(0.5f)
                service
                    .update()
                    .subscribeOnMain {
                        isEnabled = true; animate().alpha(1f)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        service.checkHasUpdates().subscribeOnMain { view.isVisible = it }
    }
}
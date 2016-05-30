package y2k.joyreactor.platform

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.CreateCommentFragment
import y2k.joyreactor.PostLikeFragment
import y2k.joyreactor.common.ActivityLifecycleCallbacksAdapter
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.startActivity
import y2k.joyreactor.viewmodel.CreateCommentViewModel
import y2k.joyreactor.viewmodel.PostLikeViewModel
import kotlin.reflect.KClass

/**
 * Created by y2k on 10/19/15.
 */
class AndroidNavigation(app: Application) : NavigationService {

    private val fragmentManager: FragmentManager?
        get() = (currentActivity as AppCompatActivity?)?.supportFragmentManager

    internal var currentActivity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

    override fun close() {
        val dialog = fragmentManager?.findFragmentByTag("dialog")
        if (dialog == null) currentActivity?.finish()
        else (dialog as DialogFragment).dismiss()
    }

    override fun <T : Any> open(vmType: KClass<T>, argument: String) {
        sArgument = argument
        when (vmType) {
            PostLikeViewModel::class -> PostLikeFragment().show(fragmentManager, "dialog")
            CreateCommentViewModel::class -> CreateCommentFragment().show(fragmentManager, "dialog")
            else -> generalOpen(vmType)
        }
    }

    private fun <T : Any> generalOpen(vmType: KClass<T>) {
        try {
            val activityClsName = vmType.java.simpleName.replace("ViewModel", "Activity")
            val activityCls = javaClass.classLoader.loadClass(
                currentActivity!!.packageName + "." + activityClsName)
            startActivity(activityCls.kotlin)
        } catch (e: Exception) {
            throw Exception("Can't handler navigation to $vmType", e)
        }
    }

    private fun startActivity(activityType: KClass<*>) {
        currentActivity?.startActivity(activityType)
    }

    override val argument: String
        get() = sArgument

    override fun openBrowser(url: String) {
        currentActivity!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private inner class MyActivityLifecycleCallbacks : ActivityLifecycleCallbacksAdapter() {

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity?) {
            currentActivity = activity
        }

        override fun onActivityPaused(activity: Activity?) {
            if (currentActivity == activity) currentActivity = null
        }
    }

    companion object {

        // FIXME: перенести на уровень платформы
        internal var sArgument = "2294127"
    }
}
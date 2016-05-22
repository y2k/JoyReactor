package y2k.joyreactor.platform

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.*
import y2k.joyreactor.common.ActivityLifecycleCallbacksAdapter
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.startActivity
import y2k.joyreactor.viewmodel.*
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
            MessagesViewModel::class -> startActivity(MessagesActivity::class)
            GalleryViewModel::class -> startActivity(GalleryActivity::class)
            ProfileViewModel::class -> startActivity(ProfileActivity::class)
            VideoViewModel::class -> startActivity(VideoActivity::class)
            ImageViewModel::class -> startActivity(ImageActivity::class)
            LoginViewModel::class -> startActivity(LoginActivity::class)
            PostViewModel::class -> startActivity(PostActivity::class)
            else -> throw Exception("Can't handler navigation to $vmType")
        }
    }

    private fun startActivity(activityType: KClass<out Activity>) {
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
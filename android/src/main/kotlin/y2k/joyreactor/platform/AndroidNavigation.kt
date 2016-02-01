package y2k.joyreactor.platform

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.*

/**
 * Created by y2k on 10/19/15.
 */
class AndroidNavigation(app: Application) : Navigation {

    internal var currentActivity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

    override fun switchProfileToLogin() {
        currentActivity!!.startActivity(Intent(currentActivity, LoginActivity::class.java))
        currentActivity!!.finish()
    }

    override fun switchLoginToProfile() {
        currentActivity!!.startActivity(Intent(currentActivity, ProfileActivity::class.java))
        currentActivity!!.finish()
    }

    override fun closeCreateComment() {
        currentActivity!!.finish()
    }

    override fun closeAddTag() {
        AddTagDialogFragment.dismiss(currentActivity as AppCompatActivity)
    }

    override fun openPost(postId: String) {
        sPostIdArgument = postId
        currentActivity!!.startActivity(Intent(currentActivity, PostActivity::class.java))
    }

    override val argumentPostId: String
        get() = sPostIdArgument

    override fun openBrowser(url: String) {
        currentActivity!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun openVideo(postId: String) {
        // TODO:
        sPostIdArgument = postId
        currentActivity!!.startActivity(Intent(currentActivity, VideoActivity::class.java))
    }

    override fun openImageView(post: Post) {
        // TODO:
    }

    override fun openCreateComment() {
        currentActivity!!.startActivity(Intent(currentActivity, CreateCommentActivity::class.java))
    }

    private inner class MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            currentActivity = null
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }

    companion object {

        internal lateinit var sPostArgument: Post // FIXME:
        internal var sPostIdArgument = "2294127" // FIXME:
    }
}
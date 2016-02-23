package y2k.joyreactor.platform

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.*
import y2k.joyreactor.common.ActivityLifecycleCallbacksAdapter
import y2k.joyreactor.common.startActivity

/**
 * Created by y2k on 10/19/15.
 */
class AndroidNavigation(app: Application) : NavigationService {

    internal var currentActivity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

    override fun openPrivateMessages(name: String) {
        throw UnsupportedOperationException()
    }

    override fun switchProfileToLogin() {
        currentActivity?.startActivity(LoginActivity::class)
        currentActivity?.finish()
    }

    override fun switchLoginToProfile() {
        currentActivity?.startActivity(ProfileActivity::class)
        currentActivity?.finish()
    }

    override fun closeCreateComment() {
        currentActivity!!.finish()
    }

    override fun closeAddTag() {
        AddTagDialogFragment.dismiss(currentActivity as AppCompatActivity)
    }

    override fun openPostGallery() {
        currentActivity?.startActivity(GalleryActivity::class)
    }

    override fun openPost(postId: String) {
        sPostIdArgument = postId
        currentActivity?.startActivity(PostActivity::class)
    }

    override val argumentPostId: String
        get() = sPostIdArgument

    override fun openBrowser(url: String) {
        currentActivity!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun openVideo(postId: String) {
        sPostIdArgument = postId // TODO:
        currentActivity?.startActivity(VideoActivity::class)
    }

    override fun openImageView(postId: String) {
        // TODO:
    }

    override fun openCreateComment() {
        currentActivity?.startActivity(CreateCommentActivity::class)
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

        internal var sPostIdArgument = "2294127" // FIXME:
    }
}
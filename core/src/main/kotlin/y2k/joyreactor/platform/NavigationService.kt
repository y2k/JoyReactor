package y2k.joyreactor.platform

/**
 * Created by y2k on 02/10/15.
 */
interface NavigationService {

    fun openPrivateMessages(name: String)

    fun switchProfileToLogin()

    fun switchLoginToProfile()

    fun closeCreateComment()

    fun closeAddTag()

    fun openPost(postId: String)

    fun openBrowser(url: String)

    fun openVideo(postId: String)

    fun openImageView(postId: String)

    fun openCreateComment()

    fun openPostGallery()

    val argumentPostId: String

    companion object {

        val instance: NavigationService
            get() = Platform.instance.navigator
    }
}
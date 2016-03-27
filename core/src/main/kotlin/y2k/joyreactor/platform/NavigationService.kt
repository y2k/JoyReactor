package y2k.joyreactor.platform

/**
 * Created by y2k on 02/10/15.
 */
interface NavigationService {

    fun openMessages(name: String)

    fun switchProfileToLogin()

    fun switchLoginToProfile()

    fun closeCreateComment()

    fun closeAddTag()

    fun openPost(postId: Long)

    fun openBrowser(url: String)

    fun openVideo(postId: Long)

    fun openImageView(postId: Long)

    fun openCreateComment()

    fun openPostGallery()

    val argument: String

    companion object {

        val instance: NavigationService
            get() = Platform.instance.navigator
    }
}
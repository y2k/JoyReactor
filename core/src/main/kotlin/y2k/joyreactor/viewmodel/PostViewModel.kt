package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.*
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.ProfileService
import java.io.File

/**
 * Created by y2k on 2/28/16.
 */
class PostViewModel(
    private val service: PostService,
    private val userService: ProfileService,
    private val navigation: NavigationService,
    private val scope: LifeCycleService) {

    val isBusy = property(true)
    val error = property(false)
    val canCreateComments = property(false)

    val description = property("")
    val poster = property(PartialResult.inProgress<File>(0, 100))
    val posterAspect = property(1f)

    val tags = property(emptyList<String>())
    val images = property(emptyList<Image>())
    val comments = property(emptyList<Comment>())

    private val postId = navigation.argument.toLong()

    init {
        val process = service.synchronizePostWithImage(postId).pack()
        scope(Notifications.Post) {
            isBusy += process.isBusy
            error += process.finishedWithError

            poster += service.mainImageFromDisk(postId)
            images += service.getImages(postId)
            comments += service.getTopComments(10, postId)
            canCreateComments += userService.isAuthorized()

            service.getPost(postId).ui {
                posterAspect += it.imageAspectOrDefault(1f)
                description += it.title
                tags += it.tags
            }
        }
    }

    fun openInBrowser() = navigation.openBrowser("http://joyreactor.cc/post/" + postId)
    fun commentPost() = navigation.open<CreateCommentViewModel>(postId)
    fun showMoreImages() = navigation.open<GalleryViewModel>(postId)
    fun openImage(image: Image) = navigation.open<ImageViewModel>(image.fullUrl())

    fun saveToGallery() {
        isBusy += true
        service.saveImageToGallery(postId).ui { isBusy += false }
    }

    fun selectComment(comment: Comment) = navigation.open<CommentsViewModel>(comment.id)
}
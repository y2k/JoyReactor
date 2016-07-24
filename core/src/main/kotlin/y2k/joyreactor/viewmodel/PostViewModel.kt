package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.AttachmentService
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
    private val attachmentService: AttachmentService,
    scope: (String, () -> Unit) -> Unit) {

    val isBlockBusy = property(false)
    val isBusy = property(true)
    val error = property(false)
    val canCreateComments = property(false)

    val description = property("")
    val poster = property<File>()
    val posterAspect = property(1f)

    val tags = property(emptyList<String>())
    val images = property(emptyList<Image>())
    val comments = property(emptyList<Comment>())

    private val postId = navigation.argument.toLong()

    init {
        scope(service.syncPostInBackground(postId)) {
            async_ {
                service.getSyncStatus(postId).let {
                    isBusy += it.isInProgress
                    error += it.isFinishedWithError
                }

                poster += await(attachmentService.mainImageFromDisk(postId))
                images += await(service.getImages(postId))
                comments += await(service.getTopComments(postId, 10))
                canCreateComments += await(userService.isAuthorized())

                val post = await(service.getPost(postId))
                posterAspect += post.imageAspectOrDefault(1f)
                description += post.title
                tags += post.tags.toList()
            }
        }

        scope(attachmentService.saveImageKey()) {
            isBlockBusy += attachmentService.getSaveStatus()
        }
    }

    fun openInBrowser() = navigation.openBrowser("http://joyreactor.cc/post/" + postId)
    fun createComment() = navigation.openVM<CreateCommentViewModel>(postId)
    fun showMoreImages() = navigation.openVM<GalleryViewModel>(postId)
    fun openImage(image: Image) = navigation.openVM<ImageViewModel>(image.fullUrl())
    fun saveToGallery() = attachmentService.saveImageToGallery(postId)

    fun selectComment(comment: Comment) = navigation.openVM<CommentsViewModel>(comment.id)
}
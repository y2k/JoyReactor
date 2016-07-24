package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.PostData
import y2k.joyreactor.common.WorkStatus
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.Works
import java.io.File
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/28/16.
 */
class PostViewModel(
    val checkIsAuthorized: () -> CompletableFuture<Boolean>,
    val getPostData: (Long) -> CompletableFuture<PostData>,
    val syncInBackground: (Works, Any) -> Unit,
    val watchForBackground: (Works, Any, (WorkStatus) -> Unit) -> Unit,
    val getArgument: () -> Long,
    val navigateTo: (KClass<*>, Any?) -> Unit) {

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

    private val postId = getArgument()

    init {
        syncInBackground(Works.syncPost, postId)
        watchForBackground(Works.syncPost, postId) { status ->
            async_ {
                status.let {
                    isBusy += it.isInProgress
                    error += it.isFinishedWithError
                }

                canCreateComments += await(checkIsAuthorized())

                val data = await(getPostData(postId))

                poster += data.poster
                images += data.images
                comments += data.topComments

                posterAspect += data.post.imageAspectOrDefault(1f)
                description += data.post.title
                tags += data.post.tags.toList()
            }
        }

        watchForBackground(Works.saveAttachment, postId) { isBlockBusy += it.isInProgress }
    }

    fun openInBrowser() = navigateTo(BrowserViewModel::class, "http://joyreactor.cc/post/" + postId)
    fun createComment() = navigateTo(CreateCommentViewModel::class, postId)
    fun showMoreImages() = navigateTo(GalleryViewModel::class, postId)
    fun openImage(image: Image) = navigateTo(ImageViewModel::class, image.fullUrl())
    fun saveToGallery() = syncInBackground(Works.saveAttachment, postId)

    fun selectComment(comment: Comment) = navigateTo(CommentsViewModel::class, comment.id)
}
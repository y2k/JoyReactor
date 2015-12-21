package y2k.joyreactor.presenters

import y2k.joyreactor.*
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.services.PostService
import java.io.File

/**
 * Created by y2k on 28/09/15.
 */
class PostPresenter(
        private val view: PostPresenter.View,
        private val service: PostService) {

    init {
        view.setIsBusy(true)
        service.synchronizePostAsync(argumentPostId).subscribe({ post ->
            view.setIsBusy(false)
            view.updatePostInformation(post)

            service.getPostImages(post.id)
                    .subscribe({ view.updatePostImages(it) }, { it.printStackTrace() })

            service.getCommentsAsync(post.id, 0)
                    .subscribe({ view.updateComments(it) }, { it.printStackTrace() })

            service.getSimilarPosts(post.id)
                    .subscribe({ view.updateSimilarPosts(it) }, { it.printStackTrace() })

            service.mainImagePartial(post.serverId!!).subscribe({ partial ->
                if (partial.result == null) {
                    view.updateImageDownloadProgress(partial.progress, partial.max)
                } else {
                    view.updatePostImage(partial.result)
                }
            }, { it.printStackTrace() })
        }, { it.printStackTrace() })
    }

    fun selectComment(commentId: Int) {
        service.getFromCache(argumentPostId)
                .flatMap { post -> service.getCommentsAsync(post.id, commentId) }
                .subscribe({ view.updateComments(it) }) { it.printStackTrace() }
    }

    fun openPostInBrowser() {
        Navigation.getInstance().openBrowser("http://joyreactor.cc/post/" + argumentPostId)
    }

    fun saveImageToGallery() {
        view.setIsBusy(true)
        service.getFromCache(argumentPostId)
                .flatMap { post -> service.mainImage(post.serverId!!) }
                .flatMap { imageFile -> Platform.Instance.saveToGallery(imageFile) }
                .subscribe({
                    view.showImageSuccessSavedToGallery()
                    view.setIsBusy(false)
                }) { it.printStackTrace() }
    }

    private val argumentPostId: String
        get() = Navigation.getInstance().argumentPostId

    fun replyToComment(comment: Comment) {
        // TODO:
    }

    fun replyToPost() {
        // TODO:
        Navigation.getInstance().openCreateComment()
    }

    interface View {

        fun updateComments(comments: CommentGroup)

        @Deprecated("")
        fun updatePostInformation(post: Post)

        fun setIsBusy(isBusy: Boolean)

        fun showImageSuccessSavedToGallery()

        fun updatePostImages(images: List<Image>)

        fun updateSimilarPosts(similarPosts: List<SimilarPost>)

        fun updatePostImage(image: File)

        fun updateImageDownloadProgress(progress: Int, maxProgress: Int)
    }
}
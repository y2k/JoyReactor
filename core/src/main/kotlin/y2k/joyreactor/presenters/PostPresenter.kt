package y2k.joyreactor.presenters

import y2k.joyreactor.*
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.ProfileService
import java.io.File

/**
 * Created by y2k on 28/09/15.
 */
class PostPresenter(
        private val view: PostPresenter.View,
        private val service: PostService,
        private val userService: ProfileService) {

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

            userService.isAuthorized()
                    .subscribe({ if (it) view.setEnableCreateComments() }, { it.printStackTrace() })
        }, { it.printStackTrace() })
    }

    fun selectComment(commentId: Long) {
        service.getFromCache(argumentPostId)
                .flatMap { post -> service.getCommentsAsync(post.id, commentId) }
                .subscribe({ view.updateComments(it) }) { it.printStackTrace() }
    }

    fun openPostInBrowser() {
        Navigation.instance.openBrowser("http://joyreactor.cc/post/" + argumentPostId)
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
        get() = Navigation.instance.argumentPostId

    fun replyToComment(comment: Comment) {
        // TODO:
    }

    fun replyToPost() {
        // TODO:
        Navigation.instance.openCreateComment()
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

        fun setEnableCreateComments()
    }
}
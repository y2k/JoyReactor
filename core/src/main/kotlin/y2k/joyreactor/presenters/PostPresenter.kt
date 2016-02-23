package y2k.joyreactor.presenters

import y2k.joyreactor.*
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.*
import y2k.joyreactor.platform.NavigationService
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
    private val userService: ProfileService,
    private val navigation: NavigationService) {

    init {
        view.setIsBusy(true)
        service
            .synchronizePostAsync(argumentPostId)
            .subscribeOnMain { post ->
                view.updatePostInformation(post)

                service
                    .getPostImages()
                    .subscribeOnMain { view.updatePostImages(it) }

                service
                    .getCommentsAsync(post.id, 0)
                    .subscribeOnMain {
                        view.updateComments(it)
                        view.setIsBusy(false)
                    }

                service
                    .getSimilarPosts(post.id)
                    .subscribeOnMain { view.updateSimilarPosts(it) }

                service
                    .mainImagePartial(post.serverId)
                    .subscribeOnMain { partial ->
                        if (partial.result == null) {
                            view.updateImageDownloadProgress(partial.progress, partial.max)
                        } else {
                            view.updatePostImage(partial.result)
                        }
                    }

                userService
                    .isAuthorized()
                    .subscribeOnMain { if (it) view.setEnableCreateComments() }
            }
    }

    fun selectComment(commentId: Long) {
        service
            .getFromCache(argumentPostId)
            .flatMap { post -> service.getCommentsAsync(post.id, commentId) }
            .subscribeOnMain { view.updateComments(it) }
    }

    fun openPostInBrowser() {
        NavigationService.instance.openBrowser("http://joyreactor.cc/post/" + argumentPostId)
    }

    fun saveImageToGallery() {
        view.setIsBusy(true)
        service.getFromCache(argumentPostId)
            .flatMap { post -> service.mainImage(post.serverId!!) }
            .flatMap { imageFile -> Platform.instance.saveToGallery(imageFile) }
            .subscribeOnMain {
                view.showImageSuccessSavedToGallery()
                view.setIsBusy(false)
            }
    }

    private val argumentPostId: String
        get() = NavigationService.instance.argumentPostId

    fun replyToComment(comment: Comment) {
        // TODO:
    }

    fun replyToPost() {
        // TODO:
        NavigationService.instance.openCreateComment()
    }

    fun showMoreImages() {
        navigation.openPostGallery()
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
package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.await
import y2k.joyreactor.common.binding
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.CommentGroup
import y2k.joyreactor.model.EmptyGroup
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.platform.open
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.ProfileService
import java.io.File

/**
 * Created by y2k on 2/28/16.
 */
class PostViewModel(
    private val service: PostService,
    private val userService: ProfileService,
    private val navigation: NavigationService) {

    val isBusy = binding(false)
    val comments = binding<CommentGroup>(EmptyGroup())
    val description = binding("")

    val poster = binding(PartialResult.inProgress<File>(0, 100))
    val posterAspect = binding(1f)

    val tags = binding(emptyList<String>())

    val images = binding(emptyList<Image>())

    val error = binding(false)

    init {
        isBusy += true
        service
            .synchronizePostAsync(navigation.argument)
            .await({ post ->
                posterAspect += post.image?.aspect ?: 1f
                service.getPostImages().await { images += it }

                description += post.title
                tags += post.tags

                service
                    .getCommentsAsync(post.id, 0)
                    .await {
                        comments += it
                        isBusy += false
                    }

                service
                    .mainImagePartial(post.id)
                    .await { poster += it }

                //                userService
                //                    .isAuthorized()
                //                    .subscribeOnMain { if (it) view.setEnableCreateComments() }
            }, {
                it.printStackTrace()
                error += true
            })
    }

    fun showMoreImages() {
        navigation.open<GalleryViewModel>()
    }

    fun saveToGallery() {
        isBusy += true
        service
            .getFromCache(navigation.argument)
            .flatMap { service.mainImage(it.id) }
            .flatMap { Platform.instance.saveToGallery(it) }
            .await { isBusy += false }
    }

    fun openInBrowser() {
        navigation.openBrowser("http://joyreactor.cc/post/" + navigation.argument)
    }

    fun selectComment(position: Int) {
        TODO()
    }

    fun selectComment(comment: Comment) {
        service
            .getCommentsAsync(comment.postId, comments.value.getNavigation(comment))
            .await { comments += it }
    }
}
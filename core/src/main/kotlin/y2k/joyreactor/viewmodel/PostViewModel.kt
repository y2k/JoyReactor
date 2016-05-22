package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.await
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.common.subscribe
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.CommentGroup
import y2k.joyreactor.model.EmptyGroup
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
    private val lifeCycle: LifeCycleService) {

    val isBusy = property(false)
    val comments = property<CommentGroup>(EmptyGroup())
    val description = property("")

    val poster = property(PartialResult.inProgress<File>(0, 100))
    val posterAspect = property(1f)

    val tags = property(emptyList<String>())

    val images = property(emptyList<Image>())

    val error = property(false)

    val canCreateComments = property(false)

    init {
        isBusy += true
        service
            .synchronizePost(navigation.argument.toLong())
            .await({ isBusy += false }, {
                it.printStackTrace()
                error += true
            })

        service
            .getPost(navigation.argument.toLong())
            .subscribe(lifeCycle) { post ->
                posterAspect += post.imageAspectOrDefault(1f)
                description += post.title
                tags += post.tags

                service.mainImagePartial(post.id).await { poster += it }
            }

        service.getImages(navigation.argument.toLong())
            .subscribe(lifeCycle) { images += it }

        userService.isAuthorized().await { canCreateComments += it }

        service.getComments(navigation.argument.toLong(), 0)
            .subscribe(lifeCycle) { comments += it }
    }

    fun commentPost() {
        navigation.open<CreateCommentViewModel>(navigation.argument)
    }

    fun showMoreImages() {
        navigation.open<GalleryViewModel>()
    }

    fun saveToGallery() {
        isBusy += true
        service
            .saveImageToGallery(navigation.argument.toLong())
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
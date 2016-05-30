package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.await
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
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
    private val lifeCycle: LifeCycleService) {

    val isBusy = property(false)
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
        isBusy += true
        service
            .synchronizePost(postId)
            .await({
                isBusy += false
            }, {
                it.printStackTrace()
                error += true
            })

        lifeCycle.register(Notifications.Post) {
            service.mainImageFromDisk(postId)
                .await { poster += it }
            service.getImages(postId)
                .await { images += it }
            service.getTopComments(10, postId)
                .await { comments += it }
            userService.isAuthorized()
                .await { canCreateComments += it }

            service
                .getPost(postId)
                .await { post ->
                    posterAspect += post.imageAspectOrDefault(1f)
                    description += post.title
                    tags += post.tags
                }
        }
    }

    fun commentPost() {
        navigation.open<CreateCommentViewModel>(navigation.argument)
    }

    fun showMoreImages() {
        navigation.open<GalleryViewModel>()
    }

    fun saveToGallery() {
        isBusy += true
        service.saveImageToGallery(postId)
            .await { isBusy += false }
    }

    fun openInBrowser() {
        navigation.openBrowser("http://joyreactor.cc/post/" + postId)
    }

    fun selectComment(position: Int) {
        TODO()
    }

    fun selectComment(comment: Comment) {
        navigation.open<CommentsViewModel>(postId.toString())
    }
}
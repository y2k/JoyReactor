package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
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
            .ui({ isBusy += false }, {
                it.printStackTrace()
                error += true
            })

        lifeCycle.register(Notifications.Post) {
            poster += service.mainImageFromDisk(postId)
            images += service.getImages(postId)
            comments += service.getTopComments(10, postId)
            canCreateComments += userService.isAuthorized().toSingle()

            service.getPost(postId).ui { post ->
                posterAspect += post.imageAspectOrDefault(1f)
                description += post.title
                tags += post.tags
            }
        }
    }

    fun openInBrowser() = navigation.openBrowser("http://joyreactor.cc/post/" + postId)
    fun commentPost() = navigation.open<CreateCommentViewModel>(navigation.argument)
    fun showMoreImages() = navigation.open<GalleryViewModel>()
    fun openImage(image: Image) = navigation.open<ImageViewModel>(image.fullUrl())

    fun saveToGallery() {
        isBusy += true
        service.saveImageToGallery(postId).ui { isBusy += false }
    }

    fun selectComment(comment: Comment) = navigation.open<CommentsViewModel>(postId.toString())
}
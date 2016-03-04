package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.platform.Platform
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
    val comments = binding(emptyList<Comment>())
    val description = binding("")

    val poster = binding(PartialResult.inProgress<File>(0, 100))
    val posterAspect = binding(1f)

    val tags = binding(emptyList<String>())

    val images = binding(emptyList<Image>())

    val error = binding(false)

    init {
        isBusy.value = true
        service
            .synchronizePostAsync(navigation.argument)
            .subscribeOnMain({ post ->
                posterAspect.value = post.image?.aspect ?: 1f
                service.getPostImages().subscribeOnMain { images.value = it }

                description.value = post.title
                tags.value = post.tags

                service
                    .getCommentsAsync(post.id, 0)
                    .subscribeOnMain { group ->
                        //                        comments.value = it
                        comments.value = (0..group.size() - 1).map { group[it] }

                        isBusy.value = false
                    }

                service
                    .mainImagePartial(post.serverId)
                    .subscribeOnMain { poster.value = it }

                //                userService
                //                    .isAuthorized()
                //                    .subscribeOnMain { if (it) view.setEnableCreateComments() }
            }, {
                it.printStackTrace()
                error.value = true
            })
    }

    fun showMoreImages() {
        navigation.openPostGallery()
    }

    fun saveToGallery() {
        isBusy.value = true
        service
            .getFromCache(navigation.argument)
            .flatMap { service.mainImage(it.serverId) }
            .flatMap { Platform.instance.saveToGallery(it) }
            .subscribeOnMain { isBusy.value = false }
    }

    fun openInBrowser() {
        navigation.openBrowser("http://joyreactor.cc/post/" + navigation.argument)
    }
}
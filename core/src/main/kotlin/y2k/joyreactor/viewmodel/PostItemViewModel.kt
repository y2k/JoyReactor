package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 10/07/16.
 */
class PostItemViewModel(
    private val navigation: NavigationService,
    private val postService: PostService,
    val post: Post) {

    fun postClicked() {
        navigation.openVM<PostViewModel>(post.id)
    }

    fun playClicked() {
        if (post.image?.isAnimated ?: false) navigation.openVM<VideoViewModel>(post.id)
        else navigation.openVM<ImageViewModel>(post.image!!.fullUrl())
    }

    fun changeLike() {
        navigation.openVM<PostLikeViewModel>("" + post.id)
    }

    fun toggleFavorite() {
        postService.toggleFavorite(post.id)
    }
}
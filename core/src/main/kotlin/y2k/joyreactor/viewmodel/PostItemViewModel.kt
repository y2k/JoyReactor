package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.ui
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
        navigation.open<PostViewModel>(post.id)
    }

    fun playClicked() {
        if (post.image?.isAnimated ?: false) navigation.open<VideoViewModel>(post.id)
        else navigation.open<ImageViewModel>(post.image!!.fullUrl())
    }

    fun changeLike() {
        navigation.open<PostLikeViewModel>("" + post.id)
    }

    fun toggleFavorite() {
        postService.toggleFavorite(post.id).ui {}
    }
}
package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 4/24/16.
 */
class PostLikeViewModel(
    private val service: PostService,
    private val navigation: NavigationService) {

    val isBusy = property(false)
    val isError = property(false)

    fun like() = updatePost(true)

    fun dislike() = updatePost(false)

    private fun updatePost(like: Boolean) {
        isBusy += true
        isError += false
        service
            .updatePostLike(getPostId(), like)
            .ui({
                navigation.close()
            }, {
                it.printStackTrace()
                isBusy += false
                isError += true
            })
    }

    private fun getPostId(): Long = navigation.argument.toLong()
}
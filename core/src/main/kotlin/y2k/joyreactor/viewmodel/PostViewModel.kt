package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Post
import y2k.joyreactor.platform.NavigationService
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
    val postData = binding(null as Post?)
    val comments = binding(emptyList<Comment>())
    val description = binding("")

    val poster = binding(PartialResult.inProgress<File>(0, 100))
    val posterAspect = binding(1f)

    init {
        // TODO:
        isBusy.value = true
        service
            .synchronizePostAsync(navigation.argument)
            .subscribeOnMain { post ->
                postData.value = post

                //                service
                //                    .getPostImages()
                //                    .subscribeOnMain { view.updatePostImages(it) }

                description.value = post.title

                posterAspect.value = post.image?.aspect ?: 1f

                service
                    .getCommentsAsync(post.id, 0)
                    .subscribeOnMain { group ->
                        //                        comments.value = it
                        comments.value = (0..group.size() - 1).map { group[it] }

                        isBusy.value = false
                    }

                service
                    .mainImagePartial(post.serverId)
                    .subscribeOnMain { partial ->
                        poster.value = partial
                        //                        if (partial.result == null) {
                        //                            view.updateImageDownloadProgress(partial.progress, partial.max)
                        //                        } else {
                        //                            view.updatePostImage(partial.result)
                        //                        }
                    }


                //                service
                //                    .getSimilarPosts(post.id)
                //                    .subscribeOnMain { view.updateSimilarPosts(it) }
                //
                //                service
                //                    .mainImagePartial(post.serverId)
                //                    .subscribeOnMain { partial ->
                //                        if (partial.result == null) {
                //                            view.updateImageDownloadProgress(partial.progress, partial.max)
                //                        } else {
                //                            view.updatePostImage(partial.result)
                //                        }
                //                    }
                //
                //                userService
                //                    .isAuthorized()
                //                    .subscribeOnMain { if (it) view.setEnableCreateComments() }
            }
    }
}
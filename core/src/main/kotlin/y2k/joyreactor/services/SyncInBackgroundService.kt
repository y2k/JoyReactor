package y2k.joyreactor.services

import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.WorkStatus
import y2k.joyreactor.common.async.CompletableFuture

/**
 * Created by y2k on 24/07/16.
 */
class SyncInBackgroundService(
    val synchronizeGroups: () -> CompletableFuture<*>,
    val attachmentService: AttachmentService,
    val postService: PostService,
    val tagService: TagService,
    val backgroundWorks: BackgroundWorks,
    val scope: LifeCycleService) {

    fun sync(work: Works, arg: Long) {
        val task = when (work) {
            Works.syncPostsPreloadNewPosts -> tagService.preloadNewPosts(arg)
            Works.syncPostsApplyNew -> tagService.applyNew(arg)
            Works.syncPostsLoadNextPage -> tagService.loadNextPage(arg)
            Works.syncPostsReloadFirstPage -> tagService.reloadFirstPage(arg)

            Works.syncOnePost -> postService.syncPostAsync(arg)
            Works.saveAttachment -> attachmentService.saveImageToGalleryAsync(arg)
            Works.toggleFavorite -> postService.toggleFavorite(arg)
            Works.syncGroups -> synchronizeGroups()

            else -> throw IllegalArgumentException("key: $work")
        }

        backgroundWorks.markWorkStarted(makeKey(work, arg))
        task.thenAccept { backgroundWorks.markWorkFinished(makeKey(work, arg), it.errorOrNull) }
    }

    fun watchForBackground(work: Works, arg: Long, callback: (WorkStatus) -> Unit) {
        scope(makeKey(work)) {
            val status = backgroundWorks.getStatus(makeKey(work, arg))
            callback(status)
        }
    }

    fun watchForBackground_(work: Works, callback: ((Long) -> WorkStatus) -> Unit) {
        scope(makeKey(work)) {
            callback { backgroundWorks.getStatus(makeKey(work, it)) }
        }
    }

    private fun makeKey(work: Works, arg: Any? = null) = "$work/${arg?.toString() ?: ""}"
}

enum class Works {
    syncOnePost,
    saveAttachment,
    toggleFavorite,

    syncPosts,
    syncPostsPreloadNewPosts,
    syncPostsApplyNew,
    syncPostsLoadNextPage,
    syncPostsReloadFirstPage,

    syncGroups,
}
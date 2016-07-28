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

    fun sync(work: Works, arg: Any) {
        val task = when (work) {
            Works.syncPostsPreloadNewPosts -> tagService.preloadNewPosts(arg as String)
            Works.syncPostsApplyNew -> tagService.applyNew(arg as String)
            Works.syncPostsLoadNextPage -> tagService.loadNextPage(arg as String)
            Works.syncPostsReloadFirstPage -> tagService.reloadFirstPage(arg as String)

            Works.syncOnePost -> postService.syncPostAsync(arg as Long)
            Works.saveAttachment -> attachmentService.saveImageToGalleryAsync(arg as Long)
            Works.toggleFavorite -> postService.toggleFavorite(arg as Long)
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

    fun watchForBackground_(work: Works, onChanged: ((Any) -> WorkStatus) -> Unit) {
        scope(makeKey(work)) {
            onChanged { backgroundWorks.getStatus(makeKey(work, it)) }
        }
    }

    private fun makeKey(work: Works, arg: Any? = null) = "$work${arg?.toString() ?: ""}"
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
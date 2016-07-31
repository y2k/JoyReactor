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

        val syncWorkList = listOf(
            Works.syncPostsPreloadNewPosts, Works.syncPostsApplyNew,
            Works.syncPostsLoadNextPage, Works.syncPostsReloadFirstPage)
        val key = if (syncWorkList.contains(work)) makeKey(Works.syncPosts, arg) else makeKey(work, arg)
        val broadcastKey = if (syncWorkList.contains(work)) makeKey(Works.syncPosts) else makeKey(work, arg)
        backgroundWorks.markWorkStarted(key, broadcastKey)
        task.thenAccept { backgroundWorks.markWorkFinished(key, broadcastKey, it.errorOrNull) }
    }

    fun watchForBackground(work: Works, arg: Long, callback: (WorkStatus) -> Unit) {
        scope(makeKey(work)) {
            val status = backgroundWorks.getStatus(makeKey(work, arg))
            callback(status)
        }
    }

    fun watchForBackground(work: Works, onChanged: () -> Unit) {
        scope(makeKey(work)) { onChanged() }
    }

    fun statusBackgroundTask(work: Works, arg: Any): WorkStatus {
        return backgroundWorks.getStatus(makeKey(work, arg))
    }

    private fun makeKey(work: Works, arg: Any? = null): String {
        val key = arg?.toString() ?: ""
        return "$work/$key"
    }
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
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

    fun sync(keyEnum: Works, arg: Any) {
        val key = "" + keyEnum
        backgroundWorks.markWorkStarted(toKey(arg, key))
        val task = when {
            key.startsWith("" + Works.syncPostsPreloadNewPosts) -> tagService.preloadNewPosts(arg as Long)
            key.startsWith("" + Works.syncPostsApplyNew) -> tagService.applyNew(arg as Long)
            key.startsWith("" + Works.syncPostsLoadNextPage) -> tagService.loadNextPage(arg as Long)
            key.startsWith("" + Works.syncPostsReloadFirstPage) -> tagService.reloadFirstPage(arg as Long)

            key.startsWith("" + Works.syncPost) -> postService.syncPostAsync(arg as Long)
            key.startsWith("" + Works.saveAttachment) -> attachmentService.saveImageToGalleryAsync(arg as Long)
            key.startsWith("" + Works.toggleFavorite) -> postService.toggleFavorite(arg as Long)
            key.startsWith("" + Works.syncGroups) -> synchronizeGroups()

            else -> throw IllegalArgumentException("key: $key")
        }
        task.thenAccept { backgroundWorks.markWorkFinished(toKey(arg, key), it.errorOrNull) }
    }

    fun watchForBackground(keyEnum: Works, arg: Any, f: (WorkStatus) -> Unit) {
        val key = "" + keyEnum
        scope(toKey(arg, key)) {
            val status = backgroundWorks.getStatus(toKey(arg, key))
            f(status)
        }
    }

    private fun toKey(arg: Any, key: String) = "$arg$key"
}

enum class Works {
    syncPost,
    saveAttachment,
    toggleFavorite,

    syncPosts,
    syncPostsPreloadNewPosts,
    syncPostsApplyNew,
    syncPostsLoadNextPage,
    syncPostsReloadFirstPage,

    syncGroups,
}
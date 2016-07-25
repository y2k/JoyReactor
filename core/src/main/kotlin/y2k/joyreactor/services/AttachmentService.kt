package y2k.joyreactor.services

import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.WorkStatus
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.async.then_
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.repository.Entities
import java.io.File

/**
 * Created by y2k on 16/07/16.
 */
class AttachmentService(
    private val requestImage: (String, Boolean) -> CompletableFuture<File?>,
    private val entities: Entities,
    private val backgroundWorks: BackgroundWorks,
    private val platform: Platform) {

    fun getVideoFile(postId: Long): CompletableFuture<File?> {
        return entities
            .useAsync { Posts.getById(postId) }
            .thenAsync { requestImage(it.image!!.mp4, true) }
    }

    fun getDownloadStatus(postId: Long): WorkStatus {
        return backgroundWorks.getStatus(postId.toKey())
    }

    fun downloadInBackground(postId: Long): String {
        backgroundWorks.markWorkStarted(postId.toKey())
        entities
            .useAsync { Posts.getById(postId) }
            .thenAsync { requestImage(it.image!!.mp4, false) }
            .then_ { backgroundWorks.markWorkFinished(postId.toKey(), it.error) }
        return postId.toKey()
    }

    fun mainImageFromDisk(serverPostId: Long): CompletableFuture<File?> {
        return entities
            .useAsync { Posts.getById(serverPostId) }
            .thenAsync {
                when {
                    it.image == null -> CompletableFuture.completedFuture(null as File?)
                    it.image.isAnimated -> {
                        requestImage(it.image.original, true)
                            .thenAsync { platform.createTmpThumbnail(it!!) }
                    }
                    else -> requestImage(it.image.original, true)
                }
            }
    }

    private fun Long.toKey() = "sync-video-" + this

    fun saveImageToGallery(postId: Long) {
        backgroundWorks.markWorkStarted(saveImageKey())
        entities
            .useAsync { Posts.getById(postId) }
            .thenAsync { requestImage(it.image!!.fullUrl(null), false) }
            .thenAsync { platform.saveToGallery(it!!) }
            .thenAccept { backgroundWorks.markWorkFinished(saveImageKey(), it.errorOrNull) }
    }

    fun saveImageToGalleryAsync(postId: Long): CompletableFuture<*> {
        return entities
            .useAsync { Posts.getById(postId) }
            .thenAsync { requestImage(it.image!!.fullUrl(null), false) }
            .thenAsync { platform.saveToGallery(it!!) }
    }

    fun getSaveStatus(): Boolean = backgroundWorks.getStatus(saveImageKey()).isInProgress

    fun saveImageKey() = "save-attachment"
}
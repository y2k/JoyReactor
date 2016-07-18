package y2k.joyreactor.common.platform

import com.j256.ormlite.support.ConnectionSource
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.services.ReportService
import java.io.File

/**
 * Created by y2k on 29/09/15.
 */
interface Platform {

    val currentDirectory: File

    val navigator: NavigationService

    fun loadFromBundle(name: String, ext: String): ByteArray

    fun saveToGallery(imageFile: File): CompletableFuture<*>

    fun buildConnection(file: File): ConnectionSource

    fun <T> decodeImageAsync(path: File): CompletableFuture<T?>

    fun makeReportService(): ReportService

    fun createTmpThumbnail(videoFile: File): CompletableFuture<File>
}
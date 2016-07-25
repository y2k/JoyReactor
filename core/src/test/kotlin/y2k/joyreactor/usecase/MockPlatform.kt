package y2k.joyreactor.usecase

import com.j256.ormlite.support.ConnectionSource
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.ReportService
import java.io.File

class MockPlatform() : Platform {

    override val currentDirectory: File = createTempFile().parentFile

    override val navigator: NavigationService
        get() = throw UnsupportedOperationException()

    override fun loadFromBundle(name: String, ext: String): ByteArray {
        TODO()
    }

    override fun saveToGallery(imageFile: File): CompletableFuture<*> {
        TODO()
    }

    override fun buildConnection(file: File): ConnectionSource {
        TODO()
    }

    override fun <T> decodeImageAsync(path: File): CompletableFuture<T?> {
        TODO()
    }

    override fun makeReportService(): ReportService {
        TODO()
    }

    override fun createTmpThumbnail(videoFile: File): CompletableFuture<File> {
        TODO()
    }
}
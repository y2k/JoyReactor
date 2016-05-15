package y2k.joyreactor.platform

import com.j256.ormlite.support.ConnectionSource
import org.robovm.apple.foundation.*
import org.robovm.apple.uikit.UIImage
import rx.Observable
import y2k.joyreactor.common.ioUnitObservable
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.Platform
import java.io.File

/**
 * Created by y2k on 29/09/15.
 */
class PlatformImpl : Platform {

    override fun <T> decodeImage(path: File): T {
        TODO()
    }

    override fun buildConnection(file: File): ConnectionSource {
        throw UnsupportedOperationException()
    }

    override val currentDirectory: File
        get() = CURRENT_DIRECTORY

    override val navigator: NavigationService
        get() = StoryboardNavigation()

    override fun loadFromBundle(name: String, ext: String): ByteArray {
        val path = NSBundle.getMainBundle().findResourcePath(name, ext)
        return NSData.read(File(path)).bytes
    }

    override fun saveToGallery(imageFile: File): Observable<*> {
        return ioUnitObservable {
            UIImage(imageFile).saveToPhotosAlbum(null)
        }
    }

    companion object {

        private val CURRENT_DIRECTORY = dogGetCurrentDirectory()

        private fun dogGetCurrentDirectory(): File {
            val dirs = NSPathUtilities.getSearchPathForDirectoriesInDomains(
                NSSearchPathDirectory.DocumentDirectory,
                NSSearchPathDomainMask.UserDomainMask, true)
            return File(dirs[0])
        }
    }
}
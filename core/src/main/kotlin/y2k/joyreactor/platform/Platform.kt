package y2k.joyreactor.platform

import com.j256.ormlite.support.ConnectionSource
import rx.Observable

import java.io.File

/**
 * Created by y2k on 29/09/15.
 */
abstract class Platform {

    abstract val currentDirectory: File

    abstract val navigator: NavigationService

    abstract fun loadFromBundle(name: String, ext: String): ByteArray

    abstract fun saveToGallery(imageFile: File): Observable<*>

    abstract fun buildConnection(file: File): ConnectionSource

    companion object {

        lateinit var instance: Platform
    }
}
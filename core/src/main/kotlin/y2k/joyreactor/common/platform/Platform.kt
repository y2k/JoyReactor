package y2k.joyreactor.common.platform

import com.j256.ormlite.support.ConnectionSource
import rx.Observable

import java.io.File

/**
 * Created by y2k on 29/09/15.
 */
interface Platform {

    val currentDirectory: File

    val navigator: NavigationService

    fun loadFromBundle(name: String, ext: String): ByteArray

    fun saveToGallery(imageFile: File): Observable<*>

    fun buildConnection(file: File): ConnectionSource

    fun <T> decodeImage(path: File): T
}
package y2k.joyreactor

import android.app.Application
import android.provider.MediaStore
import rx.Observable
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.platform.AndroidNavigation
import y2k.joyreactor.platform.HandlerSchedulerFactory
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.platform.Platform
import java.io.File

/**
 * Created by y2k on 9/26/15.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        ForegroundScheduler.setInstance(HandlerSchedulerFactory().make())
        Platform.instance = object : Platform() {

            override val currentDirectory: File = filesDir

            override val navigator: Navigation = AndroidNavigation(this@App)

            override fun loadFromBundle(name: String, ext: String): ByteArray {
                return assets.open(name + "." + ext).use { it.readBytes() }
            }

            override fun saveToGallery(imageFile: File): Observable<*> {
                return ioObservable {
                    MediaStore.Images.Media.insertImage(contentResolver, imageFile.absolutePath, null, null)
                }
            }
        }
    }

    companion object {

        lateinit var instance: App
    }
}
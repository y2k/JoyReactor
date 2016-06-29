package y2k.joyreactor.common

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.j256.ormlite.android.AndroidConnectionSource
import com.j256.ormlite.support.ConnectionSource
import rx.Completable
import rx.Single
import y2k.joyreactor.App
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.platform.AndroidNavigation
import y2k.joyreactor.platform.AndroidReportService
import y2k.joyreactor.services.ReportService
import java.io.File

/**
 * Created by y2k on 5/11/16.
 */
class AndroidPlatform(private val app: Application) : Platform {

    override fun createTmpThumbnail(videoFile: File): Single<File> {
        return ioSingle {
            val thumb = ThumbnailUtils.createVideoThumbnail(
                videoFile.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
            createTempFile().apply {
                outputStream().use { thumb.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            }
        }
    }

    override fun makeReportService(): ReportService = AndroidReportService()

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeImage(path: File): T {
        return BitmapFactory.decodeFile(path.absolutePath) as T
    }

    override fun buildConnection(file: File): ConnectionSource {
        val database = SQLiteDatabase.openDatabase(file.absolutePath, null,
            SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.CREATE_IF_NECESSARY)
        return AndroidConnectionSource(database)
    }

    override val currentDirectory: File = app.filesDir

    override val navigator: NavigationService = AndroidNavigation(App.instance)

    override fun loadFromBundle(name: String, ext: String): ByteArray {
        return app.assets.open(name + "." + ext).use { it.readBytes() }
    }

    override fun saveToGallery(imageFile: File): Completable {
        return ioCompletable {
            MediaStore.Images.Media.insertImage(app.contentResolver, imageFile.absolutePath, null, null)
        }
    }
}
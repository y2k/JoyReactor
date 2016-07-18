package y2k.joyreactor.common

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.j256.ormlite.android.AndroidConnectionSource
import com.j256.ormlite.support.ConnectionSource
import y2k.joyreactor.App
import y2k.joyreactor.R
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.platform.AndroidNavigation
import y2k.joyreactor.platform.AndroidReportService
import y2k.joyreactor.platform.ImageViewMetaStorage
import y2k.joyreactor.services.ImageService
import y2k.joyreactor.services.ReportService
import java.io.File

/**
 * Created by y2k on 5/11/16.
 */
class AndroidPlatform(private val app: Application) : Platform {

    init {
        ServiceLocator.register<ImageService.MetaStorage> { ImageViewMetaStorage() }
    }

    override fun createTmpThumbnail(videoFile: File): CompletableContinuation<File> {
        return runAsync {
            val thumb = ThumbnailUtils.createVideoThumbnail(
                videoFile.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
            createTempFile().apply {
                outputStream().use { thumb.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            }
        }
    }

    override fun makeReportService(): ReportService = AndroidReportService()

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeImageAsync(path: File): CompletableContinuation<T?> {
        return runAsync { BitmapFactory.decodeFile(path.absolutePath) as T }
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

    override fun saveToGallery(imageFile: File): CompletableContinuation<*> {
        return async_ {
            val permissionCheck = ContextCompat.checkSelfPermission(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                getActivity()?.let {
                    ActivityCompat.requestPermissions(it, perms, 1)
                    it.runOnUiThread { Toast.makeText(app, R.string.allow_permission_and_try_again, Toast.LENGTH_LONG).show() }
                }
            } else {
                MediaStore.Images.Media.insertImage(app.contentResolver, imageFile.absolutePath, null, null)
            }
        }
    }

    private fun getActivity(): Activity? = (navigator as AndroidNavigation).currentActivity
}
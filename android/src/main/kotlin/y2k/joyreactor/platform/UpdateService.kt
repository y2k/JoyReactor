package y2k.joyreactor.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.json.JSONObject
import rx.Observable
import y2k.joyreactor.BuildConfig
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.ioUnitObservable
import java.io.File
import java.net.URL

/**
 * Created by y2k on 05/02/16.
 */
class UpdateService(private val context: Context) {

    fun checkHasUpdates(): Observable<Boolean> {
        return ioObservable {
            BuildConfig.VERSION_NAME != getLatestRelease().getString("tag_name")
        }
    }

    fun update(): Observable<Unit> {
        return ioUnitObservable {
            val url = getLatestRelease().getJSONArray("assets").getJSONObject(0).getString("url")
            val file = File(context.externalCacheDir, "update.apk")

            URL(url).openConnection()
                .apply { addRequestProperty("Accept", "application/octet-stream") }
                .inputStream.use { stream -> file.outputStream().use { stream.copyTo(it) } }

            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                })
        }
    }

    private fun getLatestRelease(): JSONObject {
        val json = URL("https://api.github.com/repos/y2k/JoyReactor/releases/latest").readText()
        return JSONObject(json)
    }
}
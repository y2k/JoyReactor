package y2k.joyreactor.platform

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import org.json.JSONObject
import y2k.joyreactor.BuildConfig
import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.runAsync
import java.io.File
import java.net.URL

/**
 * Created by y2k on 05/02/16.
 */
class UpdateService(
    private val backgroundWorks: BackgroundWorks,
    private val context: Context) {

    private val url = "https://api.github.com/repos/y2k/JoyReactor/releases/latest"
    private val prefs = context.getSharedPreferences("update-service", 0)
    private val key = "update-app"

    fun isCheckInProgress(): Boolean = backgroundWorks.getStatus(key).isInProgress

    fun downloadUpdate(): Any {
        async_ {
            if (backgroundWorks.getStatus(key).isInProgress) return@async_
            backgroundWorks.markWorkStarted(key)

            if (System.currentTimeMillis() - prefs.getLong("last-check", 0) < AlarmManager.INTERVAL_FIFTEEN_MINUTES)
                return@async_

            prefs.edit()
                .putLong("last-check", System.currentTimeMillis())
                .putString("server-version", await(getLatestRelease()).getString("tag_name"))
                .apply()
            BuildConfig.VERSION_NAME.code < prefs.getString("server-version", "").code

            val url = await(getLatestRelease())
                .getJSONArray("assets").getJSONObject(0).getString("url")
            val file = File(context.externalCacheDir, "update.apk")

            URL(url).openConnection()
                .apply { addRequestProperty("Accept", "application/octet-stream") }
                .inputStream.use { stream -> file.outputStream().use { stream.copyTo(it) } }
        }

        return key
    }

    fun hasFileToInstall(): CompletableContinuation<Boolean> {
        return runAsync { File(context.externalCacheDir, "update.apk").exists() }
    }

    fun installUpdate() {
        val file = File(context.externalCacheDir, "update.apk")
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }

//    fun checkHasUpdates(): CompletableContinuation<Boolean> {
//        if (BuildConfig.DEBUG) return CompletableContinuation.just(false)
//        return runAsync {
//            synchronizeWithServer()
//            BuildConfig.VERSION_NAME.code < prefs.getString("server-version", "").code
//        }
//    }
//
//    private fun synchronizeWithServer() {
//        if (System.currentTimeMillis() - prefs.getLong("last-check", 0) < AlarmManager.INTERVAL_FIFTEEN_MINUTES) return
//        prefs.edit()
//            .putLong("last-check", System.currentTimeMillis())
//            .putString("server-version", getLatestRelease().getString("tag_name"))
//            .apply()
//    }
//
//    fun update(): CompletableContinuation<Unit> {
//        return runAsync {
//            val url = getLatestRelease().getJSONArray("assets").getJSONObject(0).getString("url")
//            val file = File(context.externalCacheDir, "update.apk")
//
//            URL(url).openConnection()
//                .apply { addRequestProperty("Accept", "application/octet-stream") }
//                .inputStream.use { stream -> file.outputStream().use { stream.copyTo(it) } }
//
//            context.startActivity(
//                Intent(Intent.ACTION_VIEW).apply {
//                    setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                })
//        }
//    }

    val String.code: Int
        get() = split('.').last().toInt()

    private fun getLatestRelease(): CompletableContinuation<JSONObject> {
        return runAsync {
            URL(url).readText().let { JSONObject(it) }
        }

//        val json = URL("https://api.github.com/repos/y2k/JoyReactor/releases/latest").readText()
//        return JSONObject(json)
    }
}
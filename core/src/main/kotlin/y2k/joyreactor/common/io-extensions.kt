package y2k.joyreactor.common

import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.runAsync
import java.io.File
import java.net.URLConnection

/**
 * Created by y2k on 18/07/16.
 */

fun URLConnection.downloadToFileAsync(file: File): CompletableContinuation<*> {
    return runAsync {
        inputStream.use { stream -> file.outputStream().use { stream.copyTo(it) } }
    }
}

fun URLConnection.downloadToFile(file: File) {
    inputStream.use { stream -> file.outputStream().use { stream.copyTo(it) } }
}
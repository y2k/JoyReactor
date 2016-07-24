package y2k.joyreactor.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async
import y2k.joyreactor.common.async.runAsync
import java.io.File

/**
 * Created by y2k on 24/07/16.
 */

fun decodeBitmapFile(file: File, width: Int, height: Int): CompletableFuture<Bitmap> {
    return async {
        val op = BitmapFactory.Options()
        op.inJustDecodeBounds = true
        await(decodeBitmapFile(file, op))

        op.inJustDecodeBounds = false
        op.inSampleSize = Math.max(op.outWidth / width, op.outHeight / height)

        await(decodeBitmapFile(file, op))!!
    }
}

fun decodeBitmapFile(file: File, op: BitmapFactory.Options? = null): CompletableFuture<Bitmap?> {
    return runAsync { BitmapFactory.decodeFile(file.absolutePath, op) }
}
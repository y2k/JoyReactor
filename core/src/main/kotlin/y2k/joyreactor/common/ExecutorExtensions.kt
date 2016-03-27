package y2k.joyreactor.common

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 3/27/16.
 */

fun ScheduledExecutorService.schedule(delay: Long, f: () -> Unit) {
    this.schedule(f, delay, TimeUnit.MILLISECONDS)
}

inline fun <T> logThread(message: String, f: () -> T): T {
    println("[LOG] (${Thread.currentThread().id}) START $message")
    val start = System.currentTimeMillis()
    try {
        return f()
    } finally {
        val dur = System.currentTimeMillis() - start
        println("[LOG] (${Thread.currentThread().id}) END $dur ms $message")
    }
}
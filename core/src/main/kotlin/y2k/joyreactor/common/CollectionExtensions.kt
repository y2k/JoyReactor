package y2k.joyreactor.common

import java.util.*

/**
 * Created by y2k on 1/31/16.
 */

inline fun <T, T2, R> List<Pair<T, T2>>.flatMapPair(f: (T, T2) -> List<R>): List<R> {
    return flatMap { f(it.first, it.second) }
}

fun <T, R> List<T>.join(other: List<R>): List<Pair<T, R>> {
    return flatMap { t -> other.map { t to it } }
}

fun <T> List<T>.firstOrThrow(e: Exception, f: (T) -> Boolean): T = firstOrNull(f) ?: throw e

fun <T> List<T>.unionOrdered(other: List<T>): List<T> {
    val result = this.toArrayList()
    result.addAll(other)
    return result
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun <T> List<T>.groupToPair(): List<Pair<T, T>> {
    return withIndex()
        .groupBy { it.index and 2 }
        .map { it.value[0].value to it.value[1].value }
}

//fun <T> MutableMap<T, T>.put(item: Pair<T, T>) {
//    put(item.first, item.second)
//}
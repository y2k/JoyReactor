package y2k.joyreactor.common

/**
 * Created by y2k on 1/31/16.
 */

fun <T> List<T>.unionOrdered(other: List<T>): List<T> {
    val result = this.toArrayList()
    result.addAll(other)
    return result
}
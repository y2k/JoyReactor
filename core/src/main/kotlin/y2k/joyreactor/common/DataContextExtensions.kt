package y2k.joyreactor.common

import y2k.joyreactor.services.repository.DataSet

/**
 * Created by y2k on 5/28/2016.
 */

fun <T> DataSet<T>.replaceAll(filter: Pair<String, Any>, data: List<T>) {
    remove(filter)
    data.forEach { add(it) }
}

fun <T> DataSet<T>.updateAll(filter: Pair<String, Any>, f: (T) -> T) {
    filter(filter).map { f(it) }.forEach { add(it) }
}

fun <T> DataSet<T>.first(filter: Pair<String, Any>): T {
    return filter(filter).first()
}
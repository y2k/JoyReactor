package y2k.joyreactor.common

import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto

/**
 * Created by y2k on 5/28/2016.
 */

fun <T : Dto> DataSet<T>.updateAll(filter: Pair<String, Any>, f: (T) -> T) {
    filter(filter).map { f(it) }.forEach { add(it) }
}
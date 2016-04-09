package y2k.joyreactor.services.repository.ormlite

import com.j256.ormlite.dao.Dao
import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto

/**
 * Created by y2k on 4/10/16.
 */
class OrmLiteDataSet<T : Dto>(dao: Dao<T, *>) : DataSet<T> {

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun remove(element: T) {
        throw UnsupportedOperationException()
    }

    override fun add(element: T): T {
        throw UnsupportedOperationException()
    }

    override fun filter(f: (T) -> Boolean): List<T> {
        throw UnsupportedOperationException()
    }

    override fun firstOrNull(f: (T) -> Boolean): T? {
        throw UnsupportedOperationException()
    }

    override fun toList(): List<T> {
        throw UnsupportedOperationException()
    }

    override fun forEach(f: (T) -> Unit) {
        throw UnsupportedOperationException()
    }

    override fun none(f: (T) -> Boolean): Boolean {
        throw UnsupportedOperationException()
    }

    override fun asIterable(): Iterable<T> {
        throw UnsupportedOperationException()
    }

    override fun first(f: (T) -> Boolean): T {
        throw UnsupportedOperationException()
    }

    override fun <K> groupBy(f: (T) -> K): Map<K, List<T>> {
        throw UnsupportedOperationException()
    }
}
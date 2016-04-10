package y2k.joyreactor.services.repository.arraylist

import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto
import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
class ArrayListDataSet<T : Dto>() : DataSet<T> {

    private val items = ArrayList<T>()

    override fun clear() {
        items.clear()
    }

    override fun remove(element: T) {
        items.remove(element)
    }

    override fun add(element: T): T {
        val id = (Math.random() * Long.MAX_VALUE).toLong()
        @Suppress("UNCHECKED_CAST")
        val e = if (element.id == 0L) element.identify(id)  as T else element
        items.add(e)
        return e
    }

    override fun toList(): List<T> {
        return items.toList()
    }

    override fun forEach(f: (T) -> Unit) {
        items.forEach(f)
    }

    override fun none(f: (T) -> Boolean): Boolean {
        return items.none(f)
    }

    override fun getById(id: Long): T {
        return items.first { it.id == id }
    }

    override fun getByIdOrNull(id: Long): T? {
        return items.firstOrNull { it.id == id }
    }

    override fun asIterable(): Iterable<T> {
        return items.asIterable()
    }

    override fun <K> groupBy(f: (T) -> K): Map<K, List<T>> {
        return items.groupBy(f)
    }

    override fun filter(field: String, value: Any): List<T> {
        TODO()
    }
}
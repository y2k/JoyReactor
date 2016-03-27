package y2k.joyreactor.services.repository

import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
class DataSet<T : DataSet.Dto>(val name: String) : Iterable<T> {

    private val items = ArrayList<T>()

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }

    fun clear() {
        items.clear()
    }

    fun remove(element: T) {
        items.remove(element)
    }

    fun add(element: T): T {
        val id = (Math.random() * Long.MAX_VALUE).toLong()
        @Suppress("UNCHECKED_CAST")
        val e = if (element.id == 0L) element.identify(id)  as T else element
        items.add(e)
        return e
    }

    interface Dto {

        val id: Long

        fun identify(newId: Long): Dto
    }
}
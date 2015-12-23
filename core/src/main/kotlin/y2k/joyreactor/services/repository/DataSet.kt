package y2k.joyreactor.services.repository

import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
class DataSet<T : DataSet.Dto> : Iterable<T> {

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

    fun add(element: T) {
        if (element.id == 0L) element.id = (Math.random() * Long.MAX_VALUE).toLong()
        items.add(element)
    }

    interface Dto {

        var id: Long
    }
}
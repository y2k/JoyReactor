package y2k.joyreactor.services.repository.arraylist

import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 12/23/15.
 */
class ArrayListDataSet<T : Dto>(val type: KClass<T>) : DataSet<T> {

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

    override fun filter(propertyName: String, value: Any): List<T> {
        val getter = type.java.getMethod(toGetterName(propertyName))
        return items.filter { getter(it) == value }
    }

    override fun groupBy(groupProp: String, orderProp: String): List<T> {
        val groupGetter = type.java.getMethod(toGetterName(groupProp))
        val sortGetter = type.java.getMethod(toGetterName(orderProp))

        return items
            .groupBy { groupGetter(it) }
            .map { it.value.maxBy { sortGetter(it) as Comparable<Any> }!! }
    }

    private fun toGetterName(propertyName: String): String {
        if (propertyName.startsWith("is")) return propertyName
        return "get${propertyName.substring(0..0).toUpperCase()}${propertyName.substring(1)}"
    }
}
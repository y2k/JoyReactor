package y2k.joyreactor.services.repository

/**
 * Created by y2k on 4/9/16.
 */
interface DataSet <T : Dto> {

    fun clear()

    fun remove(element: T)

    fun add(element: T): T

    fun filter(f: (T) -> Boolean): List<T>

    fun firstOrNull(f: (T) -> Boolean): T?

    fun toList(): List<T>

    fun forEach(f: (T) -> Unit)

    fun none(f: (T) -> Boolean): Boolean

    fun asIterable(): Iterable<T>

    fun getById(id: Long): T

    fun getByIdOrNull(id: Long): T?

    fun <K> groupBy(f: (T) -> K): Map<K, List<T>>
}

interface Dto {

    val id: Long

    fun identify(newId: Long): Dto
}
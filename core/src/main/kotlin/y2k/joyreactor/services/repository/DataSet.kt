package y2k.joyreactor.services.repository

/**
 * Created by y2k on 4/9/16.
 */
interface DataSet <T : Dto> {

    fun clear()

    fun remove(element: T)

    fun add(element: T): T

    fun toList(): List<T>

    fun forEach(f: (T) -> Unit)

    fun none(f: (T) -> Boolean): Boolean

    fun getById(id: Long): T

    fun getByIdOrNull(id: Long): T?

    fun filter(propertyName: String, value: Any): List<T>

    fun filter(vararg conditions: Pair<String, Any?>): List<T>

    fun groupBy(groupProp: String, orderProp: String): List<T>
}

interface Dto {

    val id: Long

    fun identify(newId: Long): Dto
}
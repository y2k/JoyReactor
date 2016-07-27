package y2k.joyreactor.services.repository

/**
 * Created by y2k on 4/9/16.
 */
interface DataSet<T> {

    fun clear()

    fun remove(element: T)

    fun remove(condition: Pair<String, Any?>)

    fun add(element: T): T

    fun toList(): List<T>

    fun forEach(f: (T) -> Unit)

    fun getById(id: Long): T

    fun getByIdOrNull(id: Long): T?

    fun filter(vararg conditions: Pair<String, Any?>): List<T>

    fun groupBy(groupProp: String, orderProp: String): List<T>
}
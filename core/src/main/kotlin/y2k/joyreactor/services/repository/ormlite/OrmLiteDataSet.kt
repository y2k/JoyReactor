package y2k.joyreactor.services.repository.ormlite

import com.j256.ormlite.dao.Dao
import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto

/**
 * Created by y2k on 4/10/16.
 */
class OrmLiteDataSet<T : Dto>(private val dao: Dao<T, Long>) : DataSet<T> {

    override fun clear() {
        dao.deleteBuilder().delete()
    }

    override fun remove(element: T) {
        dao.delete(element)
    }

    override fun add(element: T): T {
        dao.createOrUpdate(element)
        return element
    }

    override fun toList(): List<T> {
        return dao.toList()
    }

    override fun forEach(f: (T) -> Unit) {
        dao.forEach(f)
    }

    override fun none(f: (T) -> Boolean): Boolean {
        return dao.none(f)
    }

    override fun asIterable(): Iterable<T> {
        return dao
    }

    override fun getById(id: Long): T {
        return dao.queryForId(id)
    }

    override fun getByIdOrNull(id: Long): T? {
        return dao.queryForId(id)
    }

    override fun filter(propertyName: String, value: Any): List<T> {
        return dao.queryForEq(propertyName, value)
    }

    override fun groupBy(groupProp: String, orderProp: String): List<T> {
        return dao.queryBuilder()
            .orderBy(orderProp, true)
            .groupBy(groupProp)
            .query()
    }
}
package y2k.joyreactor.services.repository.arraylist

import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto
import y2k.joyreactor.services.repository.IDataContext
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 4/9/16.
 */
class ArrayListDataContext : IDataContext {

    private val tables = ArrayList<ArrayListDataSet<*>>()

    override fun saveChanges() {
        // TODO: при forEach падает
        tables.forEach { ArrayListSerializer.saveToDisk(it) }
    }

    override fun <T : Dto> register(clazz: KClass<T>): DataSet<T> {
        return ArrayListDataSet<T>().apply {
            ArrayListSerializer.loadFromDisk(this)
            tables.add(this)
        }
    }
}
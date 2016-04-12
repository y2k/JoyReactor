package y2k.joyreactor.common

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.CloseableIterator
import com.j256.ormlite.dao.Dao
import java.util.*

/**
 * Created by y2k on 4/11/16.
 */

fun <T, TD> Dao<T, TD>.queryRawList(sql: String): List<T> {
    val baseDao = this as BaseDaoImpl<T, TD>
    val tableName = baseDao.tableInfo.tableName
    val preparedSql = sql.replace("{table-name}", tableName)
    return queryRaw(preparedSql, rawRowMapper).results
}

inline fun <T, TD, R> Dao<T, TD>.autoClose(f: Iterable<T>.() -> R): R {
    val needToClose = ArrayList<CloseableIterator<T>>()
    try {
        return object : Iterable<T> {

            override fun iterator(): Iterator<T> {
                val i = closeableIterator()
                needToClose += i
                return i
            }
        }.f()
    } finally {
        needToClose.forEach { it.close() }
    }
}
package y2k.joyreactor.common

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.Dao

/**
 * Created by y2k on 4/11/16.
 */

fun <T, TD> Dao<T, TD>.queryRawList(sql: String): List<T> {
    val baseDao = this as BaseDaoImpl<T, TD>
    val tableName = baseDao.tableInfo.tableName
    val preparedSql = sql.replace("{table-name}", tableName)
    return queryRaw(preparedSql, rawRowMapper).results
}
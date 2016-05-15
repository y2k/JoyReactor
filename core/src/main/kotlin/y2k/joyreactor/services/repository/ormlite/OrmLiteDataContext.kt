package y2k.joyreactor.services.repository.ormlite

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.table.TableUtils
import y2k.joyreactor.common.ApplicationDataVersion
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto
import y2k.joyreactor.services.repository.IDataContext
import java.io.File
import kotlin.reflect.KClass

/**
 * Created by y2k on 4/10/16.
 */
class OrmLiteDataContext(val platform: Platform) : IDataContext {

    val path = File(platform.currentDirectory, "main.$ApplicationDataVersion.db")
    val connection = platform.buildConnection(path)

    override fun <T : Dto> register(type: KClass<T>): DataSet<T> {
        val dao: Dao<T, Long> = DaoManager.createDao(connection, type.java)
        TableUtils.createTableIfNotExists(connection, type.java)
        return OrmLiteDataSet(dao)
    }

    override fun close() {
        connection.close()
    }

    override fun saveChanges() {
        // Ignore
    }
}
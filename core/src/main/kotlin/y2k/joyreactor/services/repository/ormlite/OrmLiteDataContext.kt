package y2k.joyreactor.services.repository.ormlite

import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Dto
import y2k.joyreactor.services.repository.IDataContext
import java.io.File
import kotlin.reflect.KClass

/**
 * Created by y2k on 4/10/16.
 */
class OrmLiteDataContext(val platform: Platform) : IDataContext {

    override fun saveChanges() {
        // Ignore
    }

    override fun <T : Dto> register(clazz: KClass<T>): DataSet<T> {
        val path = File(platform.currentDirectory, "main.db")
        val connection = JdbcConnectionSource("jdbc:sqlite:$path")

        val dao = DaoManager.createDao(connection, clazz.java)
        TableUtils.createTableIfNotExists(connection, clazz.java)

        return OrmLiteDataSet(dao)
    }
}
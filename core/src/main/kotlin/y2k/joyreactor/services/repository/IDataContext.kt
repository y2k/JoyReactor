package y2k.joyreactor.services.repository

/**
 * Created by y2k on 4/9/16.
 */
interface IDataContext {

    fun saveChanges()

    fun <T : Dto> register(name: String): DataSet<T>
}
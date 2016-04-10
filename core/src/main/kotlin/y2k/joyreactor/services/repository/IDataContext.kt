package y2k.joyreactor.services.repository

import kotlin.reflect.KClass

/**
 * Created by y2k on 4/9/16.
 */
interface IDataContext {

    fun saveChanges()

    fun <T : Dto> register(type: KClass<T>): DataSet<T>
}
package y2k.joyreactor.platform

import y2k.joyreactor.common.ServiceLocator
import kotlin.reflect.KClass

/**
 * Created by y2k on 02/10/15.
 */
interface NavigationService {

    fun openBrowser(url: String)

    fun close()

    val argument: String

    fun <T : Any> open(vmType: KClass<T>, argument: String)

    companion object {

        val instance: NavigationService
            get() = ServiceLocator.resolve<Platform>().navigator
    }
}

inline fun <reified T : Any> NavigationService.open(argument: Any? = null) {
    open(T::class, argument.toString())
}
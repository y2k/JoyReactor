package y2k.joyreactor.common.platform

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

        @Deprecated("")
        val instance: NavigationService
            get() = ServiceLocator.resolve<y2k.joyreactor.common.platform.Platform>().navigator
    }
}

inline fun <reified T : Any> NavigationService.openVM(argument: Any? = null) {
    open(T::class, argument.toString())
}

inline fun <reified T> NavigationService.getArgument(): T {
    return when (T::class) {
        Long::class -> argument.toLong() as T
        else -> TODO()
    }
}
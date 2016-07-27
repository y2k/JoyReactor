package y2k.joyreactor.usecase

import y2k.joyreactor.common.platform.NavigationService
import kotlin.reflect.KClass

/**
 * Created by y2k on 25/07/16.
 */
class MockNavigationService : NavigationService {

    override fun invoke(p1: KClass<*>, p2: Any?) {
        TODO()
    }

    override fun openBrowser(url: String) {
        TODO()
    }

    override fun close() {
        TODO()
    }

    override val argument: String
        get() = throw UnsupportedOperationException()

    override fun <T : Any> open(vmType: KClass<T>, argument: String) {
        TODO()
    }
}
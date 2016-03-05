package y2k.joyreactor.services

import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/3/16.
 */
class LifeCycleService(
    private val broadcastService: BroadcastService) {

    private val actions = ArrayList<() -> Unit>()

    fun add(func: () -> Unit) {
        actions.add(func)
    }

    fun <T : Any> add(type: KClass<T>, func: (T) -> Unit) {
        actions.add { broadcastService.register(this, func, type) }
    }

    fun activate() {
        // TODO: Понять почему forEach вызывает падение
        for (action in actions) action()
    }

    fun deactivate() {
        broadcastService.unregister(this)
    }

    companion object {

        val Stub = LifeCycleService(BroadcastService())
    }
}
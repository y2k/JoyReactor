package y2k.joyreactor.services

import y2k.joyreactor.common.Notifications
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/3/16.
 */
class LifeCycleService(
    private val broadcastService: BroadcastService) {
    private val actions = ArrayList<Pair<Any?, () -> Unit>>()

    fun register(func: () -> Unit) {
        actions.add(null to func)
    }

    inline fun <reified T : Any> register(noinline func: (T) -> Unit) {
        return register(T::class, func)
    }

    fun <T : Any> register(token: KClass<T>, func: (T) -> Unit) {
        actions.add(null to { broadcastService.register(this, token, func) })
    }

    fun register(token: Notifications, func: () -> Unit) {
        val old = actions.firstOrNull { it.first == token }
        if (old != null) {
            old.first?.let { broadcastService.unregisterToken(it) }
            actions.remove(old)
        }

        actions.add(token to { broadcastService.register<Any>(this, token, { func() }) })
    }

    fun activate() {
        // TODO: Понять почему forEach вызывает падение
        for (it in actions) it.second()
    }

    fun deactivate() {
        broadcastService.unregister(this)
    }
}
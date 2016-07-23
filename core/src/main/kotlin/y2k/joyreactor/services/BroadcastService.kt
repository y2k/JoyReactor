package y2k.joyreactor.services

import y2k.joyreactor.common.async.UI_EXECUTOR
import y2k.joyreactor.model.Group
import java.util.*

/**
 * Created by y2k on 2/3/16.
 */
object BroadcastService : Function1<Any, Unit> {

    private val observers = HashMap<Any, Observable>()
    private val registrations = HashMap<ActionObserver<*>, Any>()

    override fun invoke(message: Any) {
        when (message) {
            is String -> broadcast(message, message)
            else -> broadcast(message.javaClass, message)
        }
    }

    fun broadcastType(message: Any) {
        broadcast(message.javaClass, message)
    }

    fun broadcast(message: Any) {
        broadcast(message, message)
    }

    fun broadcast(token: Any, message: Any) {
        UI_EXECUTOR.execute {
            if (token is String) {
                observers
                    .toList()
                    .filter { val s = it.first; s is String && token.startsWith(s) }
                    .forEach { it.second.notifyObservers(message) }
            } else {
                observers[token]?.notifyObservers(message)
            }
        }
    }

    fun <T : Any> register(receiver: Any, token: Any, callback: (T) -> Unit) {
        var observable = observers[token]
        if (observable == null) {
            observable = ObservableImpl()
            observers.put(token, observable)
        }

        val o = ActionObserver(callback)
        observable.addObserver(o)
        registrations.put(o, receiver)
    }

    fun unregisterToken(token: Any) {
        observers.remove(token)?.deleteObservers()
    }

    fun unregister(receiver: Any) {
        for (key in registrations.keys.toList())
            if (registrations[key] === receiver) {
                registrations.remove(key)
                for (o in observers.values)
                    o.deleteObserver(key)
            }
    }

    abstract class SubscriptionChangeMessage<T>(val newValue: T)

    class TagSelected(var group: Group) : SubscriptionChangeMessage<Group>(group)

    private class ActionObserver<T>(private val callback: (T) -> Unit) : Observer {

        @Suppress("UNCHECKED_CAST")
        override fun update(o: Observable, arg: Any) {
            callback(arg as T)
        }
    }

    private class ObservableImpl : Observable() {

        override fun notifyObservers(arg: Any) {
            setChanged()
            super.notifyObservers(arg)
        }
    }
}
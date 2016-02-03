package y2k.joyreactor.services

import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/3/16.
 */
class LifeCycleService {

    private val actions = ArrayList<() -> Unit>()

    fun add(func: () -> Unit) {
        actions.add(func)
    }

    fun <T : Any> add(type: KClass<T>, func: (T) -> Unit) {
        actions.add { DefaultMessenger.register(this, func, type.java) }
    }

    fun activate() {
        // TODO: Понять почему forEach вызывает падение
        for (action in actions) action()
    }

    fun deactivate() {
        DefaultMessenger.unregister(this)
    }

    companion object {

        fun send(message: Any) {
            DefaultMessenger.send(message)
        }
    }

    private object DefaultMessenger {

        private val observers = HashMap<Class<*>, Observable>()
        private val registrations = HashMap<ActionObserver<*>, Any>()

        fun send(message: Any) {
            val observable = observers[message.javaClass]
            observable?.notifyObservers(message)
        }

        fun <T> register(receiver: Any, callback: (T) -> Unit, type: Class<T>) {
            var observable = observers[type]
            if (observable == null) {
                observable = ObservableImpl()
                observers.put(type, observable)
            }

            val o = ActionObserver(callback)
            observable.addObserver(o)
            registrations.put(o, receiver)
        }

        fun unregister(receiver: Any) {
            for (key in registrations.keys.toList())
                if (registrations[key] === receiver) {
                    registrations.remove(key)
                    for (o in observers.values)
                        o.deleteObserver(key)
                }
        }

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
}
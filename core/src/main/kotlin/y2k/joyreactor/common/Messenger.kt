package y2k.joyreactor.common

import java.util.*

/**
 * Created by y2k on 01/10/15.
 */
object Messenger {

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
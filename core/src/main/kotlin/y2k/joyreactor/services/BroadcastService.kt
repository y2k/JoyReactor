package y2k.joyreactor.services

import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.model.Group
import java.util.*

/**
 * Created by y2k on 2/3/16.
 */
object BroadcastService {

    private val observers = HashMap<Any, Observable>()
    private val registrations = HashMap<ActionObserver<*>, Any>()

    inline fun <reified T : Any> broadcast(message: T) {
        broadcast(message, message)
    }

    fun broadcast(token: Any, message: Any) {
        ForegroundScheduler.instance.createWorker().schedule {
            val observable = observers[token]
            observable?.notifyObservers(message)
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
        var observable = observers.remove(token)
        if (observable != null) observable.deleteObservers()
    }

    fun unregister(receiver: Any) {
        for (key in registrations.keys.toList())
            if (registrations[key] === receiver) {
                registrations.remove(key)
                for (o in observers.values)
                    o.deleteObserver(key)
            }
    }

    class TagSelected(var group: Group)

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
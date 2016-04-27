package y2k.joyreactor.services

import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Message
import java.util.*

/**
 * Created by y2k on 2/3/16.
 */
class BroadcastService {

    inline fun <reified T : Any> broadcast(message: T) {
        broadcast(T::class, message)
    }

    fun broadcast(token: Any, message: Any) {
        DefaultMessenger.send(token, message)
    }

    fun <T : Any> register(receiver: Any, callback: (T) -> Unit, token: Any) {
        DefaultMessenger.register(receiver, callback, token)
    }

    fun unregister(receiver: Any) {
        DefaultMessenger.unregister(receiver)
    }

    class ThreadSelectedMessage(val thread: Message)

    class TagSelected(var group: Group)

    // TODO: Слить с родительским классом
    private object DefaultMessenger {

        private val observers = HashMap<Any, Observable>()
        private val registrations = HashMap<ActionObserver<*>, Any>()

        fun send(token: Any, message: Any) {
            val observable = observers[token]
            observable?.notifyObservers(message)
        }

        fun <T> register(receiver: Any, callback: (T) -> Unit, token: Any) {
            var observable = observers[token]
            if (observable == null) {
                observable = ObservableImpl()
                observers.put(token, observable)
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
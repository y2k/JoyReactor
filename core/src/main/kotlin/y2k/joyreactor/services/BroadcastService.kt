package y2k.joyreactor.services

import y2k.joyreactor.Message
import y2k.joyreactor.Tag
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/3/16.
 */
class BroadcastService {

    fun broadcast(message: Any) {
        DefaultMessenger.send(message)
    }

    fun <T : Any> register(receiver: Any, callback: (T) -> Unit, type: KClass<T>) {
        DefaultMessenger.register(receiver, callback, type.java)
    }

    fun unregister(receiver: Any) {
        DefaultMessenger.unregister(receiver)
    }

    class ThreadSelectedMessage(val thread: Message)

    class TagSelected(var tag: Tag)

    // TODO: Слить с родительским классом
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
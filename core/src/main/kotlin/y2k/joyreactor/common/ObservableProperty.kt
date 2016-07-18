package y2k.joyreactor.common

import y2k.joyreactor.common.async.CompletableFuture
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by y2k on 2/23/16.
 */
class ObservableProperty<T>(initValue: T) {

    private val depProps = HashMap<ObservableProperty<T>, (T) -> Unit>()
    private val callbacks = ArrayList<(T) -> Unit>()

    var value: T by Delegates.observable(initValue) { prop, old, new ->
        if (new != old) callbacks.forEach { it(new) }
    }

    operator fun plusAssign(value: T) {
        this.value = value
    }

    operator fun plusAssign(single: CompletableFuture<T>) {
        single.thenAccept { value = it.result!! }
    }

    fun subscribe(f: (T) -> Unit) {
        callbacks += f
        f(value)
    }

    fun subscribeLazy(f: (T) -> Unit) {
        callbacks += f
    }

    fun subscribe(target: ObservableProperty<T>) {
        val f: (T) -> Unit = { target += it }
        depProps[target] = f
        callbacks += f
    }

    fun unsubscribe(target: ObservableProperty<T>) {
        val f = depProps.remove(target)
        if (f != null) callbacks -= f
    }
}

fun <T> property(defaultValue: T, f: (T) -> Unit): ObservableProperty<T> {
    return property(defaultValue).apply { subscribe { f(it) } }
}

fun <T> listProperty(): ObservableProperty<List<T>> = property(emptyList<T>())

fun <T> listProperty(f: (List<T>) -> Unit): ObservableProperty<List<T>> {
    return property(emptyList<T>()).apply { subscribe { f(it) } }
}

fun <T> property(defaultValue: T): ObservableProperty<T> = ObservableProperty(defaultValue)

fun <T> property(): ObservableProperty<T?> = ObservableProperty(null)
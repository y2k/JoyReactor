package y2k.joyreactor.common

import rx.Single
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by y2k on 2/23/16.
 */
class ObservableProperty<T>(initValue: T) {

    private val subject: Subject<T, T> = PublishSubject.create()

    private val targetMap = HashMap<ObservableProperty<T>, Subscription>()

    var value: T by Delegates.observable(initValue) { prop, old, new ->
        if (new != old) subject.onNext(new)
    }

    operator fun plusAssign(value: T) {
        this.value = value
    }

    operator fun plusAssign(single: Single<T>) = single.ui { value = it }

    fun subscribe(f: (T) -> Unit) {
        subject.subscribe(f)
        f(value)
    }

    fun subscribeLazy(f: (T) -> Unit) {
        subject.subscribe(f)
    }

    fun subscribe(target: ObservableProperty<T>) {
        val sub = subject.subscribe { target += value }
        targetMap[target] = sub
        target += value
    }

    fun unsubscribe(target: ObservableProperty<T>) {
        targetMap.remove(target)?.unsubscribe()
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
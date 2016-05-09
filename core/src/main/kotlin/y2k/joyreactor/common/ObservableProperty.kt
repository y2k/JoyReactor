package y2k.joyreactor.common

import rx.Observable
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

    fun asObservable(): Observable<T> {
        return subject;
    }
}

fun <T> property(defaultValue: T): ObservableProperty<T> {
    return ObservableProperty(defaultValue)
}

fun <T> property(): ObservableProperty<T?> {
    return ObservableProperty(null)
}
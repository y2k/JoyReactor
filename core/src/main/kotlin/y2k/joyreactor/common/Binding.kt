package y2k.joyreactor.common

import rx.subjects.PublishSubject
import rx.subjects.Subject
import kotlin.properties.Delegates

/**
 * Created by y2k on 2/23/16.
 */
class Binding<T>(private val initValue: T) {

    private val subject: Subject<T, T> = PublishSubject.create()

    var value: T by Delegates.observable(initValue) { prop, old, new ->
        if (new != old) subject.onNext(new)
    }

    fun subscribe(f: (T) -> Unit) {
        subject.subscribe(f)
        f(value)
    }
}

fun <T> binding(defaultValue: T): Binding<T> {
    return Binding(defaultValue)
}

fun <T> binding(): Binding<T?> {
    return Binding<T?>(null)
}
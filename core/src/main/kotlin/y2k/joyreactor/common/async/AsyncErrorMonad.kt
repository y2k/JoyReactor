package y2k.joyreactor.common.async

/**
 * Created by y2k on 16/07/16.
 */

class ExceptionalMonad<T>(val value: T? = null, val error: Throwable? = null) {

    fun <R> than(f: (T) -> R): ExceptionalMonad<R> {
        return if (error == null) ExceptionalMonad(f(value!!)) else ExceptionalMonad(null, error)
    }

    fun onError(onError: (Throwable) -> T): T {
        return if (error == null) value!! else onError(error)
    }
}
package y2k.joyreactor.presenters

import rx.functions.Action0
import y2k.joyreactor.common.Messenger
import java.util.*

/**
 * Created by y2k on 9/27/15.
 */
abstract class Presenter {

    internal var messages = ActivateMessageHolder()
        internal set

    open fun activate() {
        messages.activate()
    }

    fun deactivate() {
        messages.deactivate()
    }

    internal inner class ActivateMessageHolder {

        private val actions = ArrayList<Action0>()

        fun <T> add(type: Class<T>, callback: (T) -> Unit) {
            actions.add (Action0 { Messenger.register(this@Presenter, callback, type) })
        }

        fun activate() {
            for (s in actions)
                s.call()
        }

        fun deactivate() {
            Messenger.unregister(this@Presenter)
        }
    }
}
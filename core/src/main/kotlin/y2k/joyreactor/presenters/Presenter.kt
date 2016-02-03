package y2k.joyreactor.presenters

import y2k.joyreactor.common.Messenger

/**
 * Created by y2k on 9/27/15.
 */
abstract class Presenter {

    internal var messages = Messenger()
        internal set

    open fun activate() {
        messages.activate()
    }

    fun deactivate() {
        messages.deactivate()
    }
}
package y2k.joyreactor.common

import y2k.joyreactor.Tag

/**
 * Created by y2k on 06/10/15.
 */
class Messages {

    class TagSelected(var tag: Tag) {

        fun broadcast() {
            Messenger.getInstance().send(this)
        }
    }
}
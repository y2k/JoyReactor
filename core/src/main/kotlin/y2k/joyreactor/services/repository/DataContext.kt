package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.Attachment
import y2k.joyreactor.Comment
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.common.ObservableUtils
import java.util.*

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: Iterable<Post> = Repository()

    val Tags: Iterable<Tag> = Repository()

    val Comments: Iterable<Comment> = Repository()

    var Attachments: Iterable<Attachment> = Repository()

    fun saveChanges() {
        // TODO:
    }

    class Factory {

        fun make(): DataContext {
            return innerMakeDataContext()
        }

        fun <T> makeAsync(callback: (DataContext) -> T): Observable<T> {
            return ObservableUtils.func {
                callback(innerMakeDataContext())
            }
        }

        private fun innerMakeDataContext(): DataContext {
            // TODO:
            return DataContext()
        }
    }

    private class Repository<T> : ArrayList<T>() {
        // TODO:
    }
}
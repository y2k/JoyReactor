package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.*
import y2k.joyreactor.common.ObservableUtils
import java.util.*

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: Iterable<Post> = Repository()

    val Tags: Iterable<Tag> = Repository()

    val TagPosts: Iterable<TagPost> = Repository()

    //    val Comments: Iterable<Comment> = Repository()

    fun saveChanges() {
        throw  RuntimeException("not implemented") // TODO:
    }

    class Factory {

        fun make(): DataContext {
            return innerMakeDataContext()
        }

        fun <T> using(callback: (DataContext) -> T): Observable<T> {
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
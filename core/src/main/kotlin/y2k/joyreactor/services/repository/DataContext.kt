package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.*
import y2k.joyreactor.common.ObservableUtils
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: Repository<Post> = Repository()

    val Tags: Repository<Tag> = Repository()

    val TagPosts: Repository<TagPost> = Repository()

    fun saveChanges() {
        throw  RuntimeException("not implemented") // TODO:
    }

    class Factory {

        fun <T> using(callback: (DataContext) -> T): Observable<T> {
            return ObservableUtils.func {
                callback(innerMakeDataContext())
            }
        }

        fun <T> usingAction(callback: (DataContext) -> T): Observable<Void> {
            return ObservableUtils.action {
                callback(innerMakeDataContext())
            }
        }

        private fun innerMakeDataContext(): DataContext {
            throw  RuntimeException("not implemented") // TODO:
        }
    }

    class Repository<T> : Iterable<T> {

        val items = ArrayList<T>()

        override fun iterator(): Iterator<T> {
            return items.iterator()
        }

        fun clear() {
            items.clear()
        }

        fun remove(element: T) {
            items.remove(element)
        }

        fun add(element: T) {
            items.add(element)
        }

        companion object {

            val executor = Executors.newSingleThreadExecutor()
        }
    }
}
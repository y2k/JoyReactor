package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.*
import y2k.joyreactor.common.ObservableUtils
import java.util.*

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

        fun make(): DataContext {
            return innerMakeDataContext()
        }

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
            // TODO:
            return DataContext()
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

        @Deprecated("unused method", ReplaceWith("add"))
        fun addAll(items: List<T>) {
            this.items.addAll(items)
        }

        fun remove(elemenent: T) {
            items.remove(elemenent)
        }

        fun add(elemenent: T) {
            items.add(elemenent)
        }
    }
}
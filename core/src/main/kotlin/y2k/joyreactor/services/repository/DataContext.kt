package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.*
import y2k.joyreactor.common.IoUtils
import y2k.joyreactor.common.ObservableUtils
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: Repository<Post> = Repository()

    val Tags: Repository<Tag> = Repository()

    val TagPosts: Repository<TagPost> = Repository()

    fun saveChanges() {
        Posts.saveToDisk()
        Tags.saveToDisk()
        TagPosts.saveToDisk()
    }

    class Factory {

        fun <T> using(callback: (DataContext) -> T): Observable<T> {
            return ObservableUtils.func(executor, Callable {
                callback(innerMakeDataContext())
            })
        }

        fun usingAction(callback: (DataContext) -> Unit): Observable<Void> {
            return ObservableUtils.action(executor, ObservableUtils.UnsafeAction0 {
                callback(innerMakeDataContext())
            })
        }

        private fun innerMakeDataContext(): DataContext {
            val entities = DataContext()
            entities.Posts.loadFromDisk()
            entities.Tags.loadFromDisk()
            entities.TagPosts.loadFromDisk()
            return entities
        }

        private fun <T> Repository<T>.loadFromDisk() {
            File(javaClass.simpleName)
                    .inputStream()
                    .let { ObjectInputStream(it) }
                    .use { stream ->
                        while (true) {
                            try {
                                add(stream.readObject() as T)
                            } catch(e: EOFException) {
                                return
                            }
                        }
                    }
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }

    private fun <T> Repository<T>.saveToDisk() {
        File(javaClass.simpleName)
                .outputStream()
                .let { ObjectOutputStream(it) }
                .use { stream -> forEach { stream.writeObject(it) } }
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
            
            val idGenerator = Random()
        }
    }
}
package y2k.joyreactor.services.repository

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.TagPost
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.platform.Platform
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: DataSet<Post> = DataSet()

    val Tags: DataSet<Tag> = DataSet()

    val TagPosts: DataSet<TagPost> = DataSet()

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

        private fun <T : Dto> DataSet<T>.loadFromDisk() {
            File(Platform.Instance.currentDirectory, javaClass.simpleName)
                    .let { file ->
                        if (!file.exists()) emptyList()
                        else file.inputStream()
                                .let { ObjectInputStream(it) }
                                .let { stream ->
                                    val result = ArrayList<T>()
                                    while (true) {
                                        try {
                                            result.add(stream.readObject() as T)
                                        } catch(e: EOFException) {
                                            break
                                        }
                                    }
                                    result.toList()
                                }
                    }
                    .forEach { add(it) }
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }

    private fun <T : Dto> DataSet<T>.saveToDisk() {
        File(Platform.Instance.currentDirectory, javaClass.simpleName)
                .outputStream()
                .let { ObjectOutputStream(it) }
                .use { stream -> forEach { stream.writeObject(it) } }
    }

    class DataSet<T : Dto> : Iterable<T> {

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
            if (element.id == 0L) element.id = (Math.random() * Long.MAX_VALUE).toLong()
            items.add(element)
        }
    }

    interface Dto {

        var id: Long
    }
}
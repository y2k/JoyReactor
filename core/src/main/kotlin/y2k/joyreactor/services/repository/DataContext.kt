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
        Serializer.saveToDisk(Posts)
        Serializer.saveToDisk(Tags)
        Serializer.saveToDisk(TagPosts)
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
            Serializer.loadFromDisk(entities.Posts)
            Serializer.loadFromDisk(entities.Tags)
            Serializer.loadFromDisk(entities.TagPosts)
            return entities
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }

    private object Serializer {

        fun <T : DataSet.Dto> loadFromDisk(dataSet: DataSet<T>) {
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
                    .forEach { dataSet.add(it) }
        }

        fun <T : DataSet.Dto> saveToDisk(dataSet: DataSet<T>) {
            File(Platform.Instance.currentDirectory, javaClass.simpleName)
                    .outputStream()
                    .let { ObjectOutputStream(it) }
                    .use { stream -> dataSet.forEach { stream.writeObject(it) } }
        }
    }
}

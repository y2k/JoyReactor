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

    val Posts: DataSet<Post> = DataSet("posts")

    val Tags: DataSet<Tag> = DataSet("tags")

    val TagPosts: DataSet<TagPost> = DataSet("tag_posts")

    fun saveChanges() {
        Serializer.saveToDisk(Posts)
        Serializer.saveToDisk(Tags)
        Serializer.saveToDisk(TagPosts)
    }

    class Factory {

        fun <T> use(callback: (DataContext) -> T): Observable<T> {
            return ObservableUtils.func(executor, Callable {
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
            getFile(dataSet)
                    .let { if (it.exists()) it else null }
                    ?.let { file ->
                        file.inputStream()
                                .let { ObjectInputStream(it) }
                                .use { stream ->
                                    while (true) {
                                        try {
                                            dataSet.add(stream.readObject() as T)
                                        } catch(e: EOFException) {
                                            break
                                        }
                                    }
                                }
                    }
        }

        fun saveToDisk(dataSet: DataSet<*>) {
            getFile(dataSet)
                    .outputStream()
                    .let { ObjectOutputStream(it) }
                    .use { stream -> dataSet.forEach { stream.writeObject(it) } }
        }

        private fun getFile(datasSet: DataSet<*>): File {
            return File(Platform.Instance.currentDirectory, "${datasSet.name}.db")
        }
    }
}

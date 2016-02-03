package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.Message
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.TagPost
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.platform.Platform
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    val Posts: DataSet<Post> = register("posts")

    val Tags: DataSet<Tag> = register("tags")

    val TagPosts: DataSet<TagPost> = register("tag_posts")

    val Messages = register<Message>("messages")

    private val tables = ArrayList<DataSet<*>>()

    private fun <T : DataSet.Dto> register(name: String): DataSet<T> {
        return DataSet<T>(name).apply { tables.add(this) }
    }

    fun saveChanges() {
        tables.forEach { Serializer.saveToDisk(it) }
    }

    class Factory {

        fun <T> applyUse(callback: DataContext.() -> T): Observable<T> {
            return Observable
                .fromCallable { innerMakeDataContext().callback(); }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(ForegroundScheduler.instance);
        }

        fun <T> use(callback: (DataContext) -> T): Observable<T> {
            return Observable
                .fromCallable { callback(innerMakeDataContext()) }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(ForegroundScheduler.instance);
        }

        private fun innerMakeDataContext(): DataContext {
            val entities = DataContext()
            entities.tables.forEach { Serializer.loadFromDisk(it) }
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
                                    @Suppress("UNCHECKED_CAST")
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
            return File(Platform.instance.currentDirectory, "${datasSet.name}.db")
        }
    }
}

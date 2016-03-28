package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.model.Message
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.TagPost
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

    private val tables = ArrayList<DataSet<*>>()

    val Posts = register<Post>("posts")

    val Tags = register<Group>("tags")

    val TagPosts = register<TagPost>("tag_posts")

    val Messages = register<Message>("messages")

    private fun <T : DataSet.Dto> register(name: String): DataSet<T> {
        return DataSet<T>(name).apply { tables.add(this) }
    }

    fun saveChanges() {
        // TODO: при forEach падает
        for (it in tables) Serializer.saveToDisk(it)
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
            // TODO: при forEach падает
            for (it in entities.tables) Serializer.loadFromDisk(it)
            return entities
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }

    private object Serializer {

        private val version = 1

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
            return File(Platform.instance.currentDirectory, "${datasSet.name}.$version.db")
        }
    }
}

package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.GroupPost
import y2k.joyreactor.model.Message
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.repository.arraylist.ArrayListSerializer
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext {

    private val tables = ArrayList<ArrayListDataSet<*>>()

    val Posts = register<Post>("posts")

    val Tags = register<Group>("tags")

    val TagPosts = register<GroupPost>("tag_posts")

    val Messages = register<Message>("messages")

    private fun <T : Dto> register(name: String): ArrayListDataSet<T> {
        return ArrayListDataSet<T>(name).apply { tables.add(this) }
    }

    fun saveChanges() {
        // TODO: при forEach падает
        for (it in tables) ArrayListSerializer.saveToDisk(it)
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
            for (it in entities.tables) ArrayListSerializer.loadFromDisk(it)
            return entities
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }
}

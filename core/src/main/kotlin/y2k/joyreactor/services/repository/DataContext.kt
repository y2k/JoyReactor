package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.GroupPost
import y2k.joyreactor.model.Message
import y2k.joyreactor.model.Post
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext(val factory: IDataContext) {

    val Posts = factory.register<Post>("posts")

    val Tags = factory.register<Group>("tags")

    val TagPosts = factory.register<GroupPost>("tag_posts")

    val Messages = factory.register<Message>("messages")

    fun saveChanges() {
        factory.saveChanges()
    }

    class Factory(val factory: IDataContext) {

        fun <T> applyUse(callback: DataContext.() -> T): Observable<T> {
            return use { it.callback() }
        }

        fun <T> use(callback: (DataContext) -> T): Observable<T> {
            return Observable
                .fromCallable { callback(DataContext(factory)) }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(ForegroundScheduler.instance);
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }
}
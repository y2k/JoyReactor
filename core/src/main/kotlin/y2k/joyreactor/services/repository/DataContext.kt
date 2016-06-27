package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.model.*
import java.util.concurrent.Executors

/**
 * Created by y2k on 12/22/15.
 */
class DataContext(val factory: IDataContext) {

    val Posts = factory.register(Post::class)
    val Tags = factory.register(Group::class)
    val TagPosts = factory.register(GroupPost::class)
    val Messages = factory.register(Message::class)
    val comments by lazy { factory.register(Comment::class) }
    val attachments by lazy { factory.register(Attachment::class) }
    val similarPosts by lazy { factory.register(SimilarPost::class) }

    fun saveChanges() {
        factory.saveChanges()
    }

    class Factory(val factory: IDataContext) {

        fun <T> applyUse(callback: DataContext.() -> T): Observable<T> {
            return use { it.callback() }
        }

        fun <T> use(callback: (DataContext) -> T): Observable<T> {
            return Observable
                .fromCallable {
                    factory.use { callback(DataContext(it)) }
                }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(ForegroundScheduler.instance);
        }

        companion object {

            private val executor = Executors.newSingleThreadExecutor()
        }
    }
}
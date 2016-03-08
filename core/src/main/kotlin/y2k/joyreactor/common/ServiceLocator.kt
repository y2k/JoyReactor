package y2k.joyreactor.common

import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.presenters.*
import y2k.joyreactor.services.*
import y2k.joyreactor.services.requests.MessageListRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher
import y2k.joyreactor.services.synchronizers.PostMerger
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher
import y2k.joyreactor.viewmodel.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 07/12/15.
 */
object ServiceLocator {

    private val map = HashMap <KClass<*>, () -> Any>()

    init {
        register { PostViewModel(resolve(), resolve(), resolve()) }
        register { NavigationService.instance }
        register { ThreadsViewModel(resolve(), resolve(), resolve()) }
        register { MessagesViewModel(resolve(), resolve()) }

        register { MessageListRequest(resolve()) }
        register { PostMerger(resolve()) }
        register { MemoryBuffer }
        register { MyTagFetcher(resolve()) }
        register { PrivateMessageFetcher(resolve(), resolve()) }

        register { PostService(resolve(), resolve(), resolve(), resolve()) }
        register { TagService(resolve(), resolve(), resolve()) }
        register { TagListService(resolve(), resolve(), resolve()) }
        register { ProfileService(resolve(), resolve()) }
        register { UserMessagesService(resolve(), resolve(), resolve()) }
        register { CommentService(resolve(), resolve(), resolve()) }
        register { LoginViewModel(resolve(), resolve()) }
        register { TagListViewModel(resolve(), resolve(), resolve()) }
        register { GalleryViewModel(resolve()) }
        register { ImageViewModel(resolve()) }
    }

    // ==========================================
    // Presenters
    // ==========================================

    fun resolve(lifeCycleService: LifeCycleService, view: PostListPresenter.View): PostListPresenter {
        return PostListPresenter(view, resolve(), lifeCycleService)
    }

    fun resolve(view: ProfilePresenter.View): ProfilePresenter {
        return ProfilePresenter(view, resolve())
    }

    fun resolve(view: CreateCommentPresenter.View): CreateCommentPresenter {
        return CreateCommentPresenter(view, resolve(), resolve())
    }

    fun resolve(view: AddTagPresenter.View): AddTagPresenter {
        return AddTagPresenter(view, resolve())
    }

    fun resolve(view: VideoPresenter.View): VideoPresenter {
        return VideoPresenter(view, resolve())
    }

    // ==========================================
    // Private methods
    // ==========================================

    inline fun <reified T : Any> resolve(lifeCycleService: LifeCycleService): T {
        register { lifeCycleService }
        try {
            return resolve()
        } catch (e: Exception) {
            throw IllegalArgumentException("Can't resolve type ${T::class}")
        } finally {
            unregister(LifeCycleService::class)
        }
    }

    inline fun <reified T : Any> resolve(): T {
        return resolve(T::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(type: KClass<T>): T {
        return map[type]?.let { it() as T } ?: type.java.newInstance()
    }

    inline fun <reified T : Any> register(noinline factory: () -> T) {
        register(T::class, factory)
    }

    fun <T : Any> register(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }

    fun <T : Any> unregister(type: KClass<T>) {
        map.remove(type)
    }
}
package y2k.joyreactor.common

import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.presenters.*
import y2k.joyreactor.services.*
import y2k.joyreactor.services.requests.MessageListRequest
import y2k.joyreactor.services.requests.SendMessageRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher
import y2k.joyreactor.services.synchronizers.PostMerger
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher
import y2k.joyreactor.viewmodel.MessagesViewModel
import y2k.joyreactor.viewmodel.PostViewModel
import y2k.joyreactor.viewmodel.ThreadsViewModel
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 07/12/15.
 */
object ServiceLocator {

    private val map = HashMap <KClass<*>, () -> Any>()

    init {
        add { PostViewModel(resolve(), resolve(), resolve()) }
        add { NavigationService.instance }
        add { ThreadsViewModel(resolve(), resolve(), resolve()) }
        add { MessagesViewModel(resolve(), resolve()) }

        add { MessageListRequest(resolve()) }
        add { PostMerger(resolve()) }
        add { MemoryBuffer }
        add { MyTagFetcher(resolve()) }
        add { PrivateMessageFetcher(resolve(), resolve()) }

        add { PostService(resolve(), resolve(), resolve(), resolve()) }
        add { TagService(resolve(), resolve(), resolve()) }
        add { TagListService(resolve(), resolve(), resolve()) }
        add { ProfileService(resolve(), resolve()) }
        add { UserMessagesService(resolve(), resolve(), resolve()) }
        add { CommentService(resolve(), resolve(), resolve()) }
    }

    // ==========================================
    // Presenters
    // ==========================================

    fun resolve(view: GalleryPresenter.View): GalleryPresenter {
        return GalleryPresenter(view, resolve())
    }

    fun resolve(lifeCycleService: LifeCycleService, view: PostListPresenter.View): PostListPresenter {
        return PostListPresenter(view, resolve(), lifeCycleService)
    }

    fun resolve(view: PostPresenter.View): PostPresenter {
        return PostPresenter(view, resolve(), resolve(), NavigationService.instance)
    }

    fun resolve(lifeCycleService: LifeCycleService, view: TagListPresenter.View): TagListPresenter {
        return TagListPresenter(view, resolve(), resolve(), lifeCycleService)
    }

    fun resolve(view: ProfilePresenter.View): ProfilePresenter {
        return ProfilePresenter(view, resolve())
    }

    fun resolve(view: CreateCommentPresenter.View): CreateCommentPresenter {
        return CreateCommentPresenter(view, resolve(), resolve())
    }

    fun resolve(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view, resolve())
    }

    fun resolve(view: AddTagPresenter.View): AddTagPresenter {
        return AddTagPresenter(view, resolve())
    }

    fun resolve(view: ImagePresenter.View): ImagePresenter {
        return ImagePresenter(view, resolve())
    }

    fun resolve(view: VideoPresenter.View): VideoPresenter {
        return VideoPresenter(view, resolve())
    }

    // ==========================================
    // Private methods
    // ==========================================

    public inline fun <reified T : Any> resolve(lifeCycleService: LifeCycleService = LifeCycleService.Stub): T {
        return resolveOld(lifeCycleService, T::class)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> resolveOld(lifeCycleService: LifeCycleService, type: KClass<T>): T {
        add(LifeCycleService::class) { lifeCycleService }
        val instance = map[type]?.let { it() as T } ?: type.java.newInstance()
        return instance.apply { remove(LifeCycleService::class) }
    }

    private inline fun <reified T : Any> add(noinline factory: () -> T) {
        add(T::class, factory)
    }

    private fun <T : Any> add(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }

    private fun <T : Any> remove(type: KClass<T>) {
        map.remove(type)
    }
}
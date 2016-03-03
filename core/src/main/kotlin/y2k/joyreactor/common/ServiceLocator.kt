package y2k.joyreactor.common

import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.presenters.*
import y2k.joyreactor.services.*
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.*
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
        add(PostViewModel::class) {
            PostViewModel(
                resolve(PostService::class),
                resolve(ProfileService::class),
                resolve(NavigationService::class))
        }

        add(NavigationService::class) { NavigationService.instance }
        add(ThreadsViewModel::class) {
            ThreadsViewModel(
                resolve(LifeCycleService::class),
                resolve(NavigationService::class),
                resolve(UserMessagesService::class))
        }
        add(MessagesViewModel::class) {
            MessagesViewModel(resolve(NavigationService::class), resolve(UserMessagesService::class))
        }
        add(SendMessageRequest::class) { SendMessageRequest() }

        add(MessageListRequest::class) { MessageListRequest(resolve(UserImageRequest::class)) }
        add(PostMerger::class) { PostMerger(resolve(DataContext.Factory::class)) }
        add(MemoryBuffer::class) { MemoryBuffer }
        add(MyTagFetcher::class) { MyTagFetcher(resolve(DataContext.Factory::class)) }
        add(PrivateMessageFetcher::class) {
            PrivateMessageFetcher(resolve(MessageListRequest::class), resolve(DataContext.Factory::class))
        }

        add(PostService::class) {
            PostService(
                resolve(OriginalImageRequestFactory::class),
                resolve(PostRequest::class),
                resolve(MemoryBuffer::class),
                resolve(DataContext.Factory::class))
        }
        add(TagService::class) {
            TagService(
                resolve(DataContext.Factory::class),
                resolve(PostsForTagRequest::class),
                resolve(PostMerger::class))
        }
        add(TagListService::class) {
            TagListService(
                resolve(DataContext.Factory::class),
                resolve(UserNameRequest::class),
                resolve(MyTagFetcher::class))
        }
        add(ProfileService::class) {
            ProfileService(resolve(ProfileRequestFactory::class), resolve(LoginRequestFactory::class))
        }
        add(UserMessagesService::class) {
            UserMessagesService(resolve(SendMessageRequest::class), resolve(PrivateMessageFetcher::class), resolve(DataContext.Factory::class))
        }
        add(CommentService::class) {
            CommentService(
                resolve(CreateCommentRequestFactory::class),
                resolve(PostRequest::class),
                resolve(MemoryBuffer::class))
        }
    }

    // ==========================================
    // Presenters
    // ==========================================

    fun resolve(view: GalleryPresenter.View): GalleryPresenter {
        return GalleryPresenter(view, resolve(PostService::class))
    }

    fun resolve(lifeCycleService: LifeCycleService, view: PostListPresenter.View): PostListPresenter {
        return PostListPresenter(view, resolve(TagService::class), lifeCycleService)
    }

    fun resolve(view: PostPresenter.View): PostPresenter {
        return PostPresenter(view, resolve(PostService::class), resolve(ProfileService::class), NavigationService.instance)
    }

    fun resolve(lifeCycleService: LifeCycleService, view: TagListPresenter.View): TagListPresenter {
        return TagListPresenter(view,
            resolve(TagListService::class),
            resolve(BroadcastService::class),
            lifeCycleService)
    }

    fun resolve(view: ProfilePresenter.View): ProfilePresenter {
        return ProfilePresenter(view, resolve(ProfileService::class))
    }

    fun resolve(view: CreateCommentPresenter.View): CreateCommentPresenter {
        return CreateCommentPresenter(view, resolve(ProfileService::class), resolve(CommentService::class))
    }

    fun resolve(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view, resolve(ProfileService::class))
    }

    fun resolve(view: AddTagPresenter.View): AddTagPresenter {
        return AddTagPresenter(view, resolve(TagListService::class))
    }

    fun resolve(view: ImagePresenter.View): ImagePresenter {
        return ImagePresenter(view, resolve(PostService::class))
    }

    fun resolve(view: VideoPresenter.View): VideoPresenter {
        return VideoPresenter(view, resolve(PostService::class))
    }

    // ==========================================
    // Private methods
    // ==========================================

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> resolve(lifeCycleService: LifeCycleService, type: KClass<T>): T {
        add(LifeCycleService::class) { lifeCycleService }
        return resolve(type).apply { remove(LifeCycleService::class) }
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> resolve(type: KClass<T>): T {
        return map[type]?.let { it() as T } ?: type.java.newInstance()
    }

    private fun <T : Any> add(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }

    private fun <T : Any> remove(type: KClass<T>) {
        map.remove(type)
    }
}
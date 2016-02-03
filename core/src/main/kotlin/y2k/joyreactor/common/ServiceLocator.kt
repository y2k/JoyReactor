package y2k.joyreactor.common

import y2k.joyreactor.presenters.*
import y2k.joyreactor.services.*
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.*
import y2k.joyreactor.services.synchronizers.MyTagFetcher
import y2k.joyreactor.services.synchronizers.PostMerger
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 07/12/15.
 */
object ServiceLocator {

    private val map = HashMap <KClass<*>, () -> Any>()

    init {
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
            UserMessagesService(resolve(PrivateMessageFetcher::class), resolve(DataContext.Factory::class))
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

    fun resolve(view: PostListPresenter.View, lifeCycleService: LifeCycleService): PostListPresenter {
        return PostListPresenter(view, resolve(TagService::class), lifeCycleService)
    }

    fun resolve(view: PostPresenter.View): PostPresenter {
        return PostPresenter(view, resolve(PostService::class), resolve(ProfileService::class))
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

    fun resolve(lifeCycleService: LifeCycleService, view: MessagesPresenter.View): MessagesPresenter {
        return MessagesPresenter(view, resolve(UserMessagesService::class), lifeCycleService)
    }

    fun resolve(view: MessageThreadsPresenter.View): MessageThreadsPresenter {
        return MessageThreadsPresenter(view, resolve(BroadcastService::class), resolve(UserMessagesService::class))
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
    public fun <T : Any> resolve(type: KClass<T>): T {
        return map[type]?.let { it() as T } ?: type.java.newInstance()
    }

    private fun <T : Any> add(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }
}
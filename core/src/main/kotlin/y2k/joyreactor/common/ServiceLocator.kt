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
        add(OriginalImageRequestFactory::class) { OriginalImageRequestFactory() }
        add(PostRequest::class) { PostRequest() }
        add(PostsForTagRequest::class) { PostsForTagRequest() }
        add(ProfileRequestFactory::class) { ProfileRequestFactory() }
        add(LoginRequestFactory::class) { LoginRequestFactory() }
        add(UserImageRequest::class) { UserImageRequest() }
        add(MessageListRequest::class) { MessageListRequest(resolve(UserImageRequest::class)) }
        add(CreateCommentRequestFactory::class) { CreateCommentRequestFactory() }

        add(DataContext.Factory::class) { DataContext.Factory() }
        add(PostMerger::class) { PostMerger(resolve(DataContext.Factory::class)) }
        add(MemoryBuffer::class) { MemoryBuffer }
        add(MyTagFetcher::class) { MyTagFetcher(resolve(DataContext.Factory::class)) }
        add(PrivateMessageFetcher::class) {
            PrivateMessageFetcher(resolve(MessageListRequest::class), resolve(MemoryBuffer::class))
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
            TagListService(resolve(DataContext.Factory::class), resolve(MyTagFetcher::class))
        }
        add(ProfileService::class) {
            ProfileService(resolve(ProfileRequestFactory::class), resolve(LoginRequestFactory::class))
        }
        add(MessageService::class) {
            MessageService(resolve(PrivateMessageFetcher::class), resolve(MemoryBuffer::class))
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

    fun resolve(view: TagListPresenter.View, lifeCycleService: LifeCycleService): TagListPresenter {
        return TagListPresenter(view, resolve(TagListService::class), lifeCycleService)
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

    fun resolve(view: MessagesPresenter.View, lifeCycleService: LifeCycleService): MessagesPresenter {
        return MessagesPresenter(view, resolve(MessageService::class), lifeCycleService)
    }

    fun resolve(view: MessageThreadsPresenter.View): MessageThreadsPresenter {
        return MessageThreadsPresenter(view, resolve(MessageService::class))
    }

    fun resolve(view: ImagePresenter.View): ImagePresenter {
        return ImagePresenter(view, resolve(PostService::class))
    }

    fun resolve(view: VideoPresenter.View): VideoPresenter {
        return VideoPresenter(view, resolve(PostService::class))
    }

    // ==========================================
    // Services
    // ==========================================

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> resolve(type: KClass<T>): T {
        return map[type]!!() as T
    }

    private fun <T : Any> add(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }
}
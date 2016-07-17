package y2k.joyreactor.common

import y2k.joyreactor.common.http.CookieStorage
import y2k.joyreactor.common.http.DefaultHttpClient
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.*
import y2k.joyreactor.services.images.DiskCache
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.repository.IDataContext
import y2k.joyreactor.services.repository.ormlite.OrmLiteDataContext
import y2k.joyreactor.services.requests.*
import y2k.joyreactor.services.requests.parser.LikeParser
import y2k.joyreactor.services.requests.parser.PostParser
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
        registerSingleton<HttpClient> { DefaultHttpClient(CookieStorage(resolve())) }
        register { NavigationService.instance }

        register { resolve<Platform>().makeReportService() }
        register { BroadcastService }

        register { PostParser(resolve<LikeParser>()) }

        register { TokenRequest(resolve()) }
        register { ChangePostFavoriteRequest(resolve(), resolve()) }
        register { LikePostRequest(resolve(), resolve<TokenRequest>(), resolve<LikeParser>()) }
        register { MessageListRequest(resolve(), resolve()) }
        register { PostMerger(resolve(), resolve()) }
        register { MemoryBuffer }
        register { MyTagFetcher(resolve(), resolve(), resolve()) }
        register { PrivateMessageFetcher(resolve(), resolve()) }
        register { PostsForTagRequest(resolve(), resolve(), resolve()) }
        register { AddTagRequest(resolve()) }
        register { UserNameRequest(resolve()) }
        register { TagsForUserRequest(resolve(), resolve()) }
        register { OriginalImageRequestFactory(resolve(), resolve()) }
        register { PostRequest(resolve(), resolve<PostParser>()) }

        register { ProfileRequest(resolve(), resolve<UserNameRequest>()) }

        register { LoginRequestFactory(resolve()) }
        register { SendMessageRequest(resolve()) }
        register { CreateCommentRequest(resolve()) }

        register {
            PostService(
                resolve<OriginalImageRequestFactory>(),
                resolve<PostRequest>(),
                resolve(), resolve(), resolve(), resolve(), resolve())
        }
        register { TagService(resolve(), resolve(), resolve(), resolve()) }

        register { UserService(resolve(), resolve(), resolve(), resolve<MyTagFetcher>(), resolve()) }

        register { ProfileService(resolve(), resolve<ProfileRequest>(), resolve(), resolve<UserNameRequest>()) }

        register { UserMessagesService(resolve(), resolve<PrivateMessageFetcher>(), resolve(), resolve()) }

        register { CommentService(resolve<CreateCommentRequest>(), resolve()) }

        register { LoginViewModel(resolve(), resolve()) }
        register { MenuViewModel(resolve(), resolve(), resolve()) }
        register { GalleryViewModel(resolve(), resolve()) }
        register { ImageViewModel(resolve(), resolve()) }
        register { VideoViewModel(resolve(), resolve(), resolve<LifeCycleService>()) }
        register { ProfileViewModel(resolve(), resolve()) }
        register { AddTagViewModel(resolve(), resolve()) }
        register { MainViewModel(resolve(), resolve(), resolve(), resolve(), resolve(), resolve()) }
        register { PostLikeViewModel(resolve(), resolve()) }
        register { CreateCommentViewModel(resolve(), resolve(), resolve()) }
        register { CommentsViewModel(resolve(), resolve(), resolve(), resolve()) }
        register { ThreadsViewModel(resolve(), resolve(), resolve()) }
        register { MessagesViewModel(resolve(), resolve()) }
        register { PostViewModel(resolve(), resolve(), resolve(), resolve(), resolve()) }

        register { DiskCache(resolve()) }
        register { ImageService(resolve(), resolve(), resolve(), resolve()) }

        registerSingleton<IDataContext> { OrmLiteDataContext(resolve()) }
        register { Entities(resolve()) }
    }

    // ==========================================
    // Private methods
    // ==========================================

    inline fun <reified T : Any> resolve(lifeCycleService: LifeCycleService): T {
        register { lifeCycleService }
        try {
            return resolve()
        } finally {
            unregister(LifeCycleService::class)
        }
    }

    inline fun <reified T : Any> resolve(): T {
        return resolve(T::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(type: KClass<T>): T {
        try {
            return map[type]?.let { it() as T } ?: type.java.newInstance()
        } catch (e: InstantiationException) {
            throw IllegalArgumentException("Can't resolve type $type", e)
        }
    }

    inline fun <reified T : Any> registerSingleton(noinline factory: () -> T) {
        var singleton: T? = null
        register(T::class, {
            if (singleton == null) singleton = factory()
            singleton!!
        })
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
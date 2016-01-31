package y2k.joyreactor.common

import y2k.joyreactor.presenters.*
import y2k.joyreactor.services.*
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.*
import y2k.joyreactor.services.synchronizers.MyTagFetcher
import y2k.joyreactor.services.synchronizers.PostMerger
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher

/**
 * Created by y2k on 07/12/15.
 */
object ServiceLocator {

    // ==========================================
    // Presenters
    // ==========================================

    fun providePostListPresenter(view: PostListPresenter.View): PostListPresenter {
        return PostListPresenter(view, provideTagService())
    }

    fun providePostPresenter(view: PostPresenter.View): PostPresenter {
        return PostPresenter(view, providePostService(), provideProfileService())
    }

    fun provideTagListPresenter(view: TagListPresenter.View): TagListPresenter {
        return TagListPresenter(view, provideTagsService())
    }

    fun provideProfilePresenter(view: ProfilePresenter.View): ProfilePresenter {
        return ProfilePresenter(view, provideProfileService())
    }

    fun provideCreateCommentPresenter(view: CreateCommentPresenter.View): CreateCommentPresenter {
        return CreateCommentPresenter(view, provideProfileService(), provideCommentService())
    }

    fun provideLoginPresenter(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view, provideProfileService())
    }

    fun provideAddTagPresenter(view: AddTagPresenter.View): AddTagPresenter {
        return AddTagPresenter(view, provideTagsService())
    }

    fun provideMessagesPresenter(view: MessagesPresenter.View): MessagesPresenter {
        return MessagesPresenter(view, provideMessageService())
    }

    fun provideMessageThreadsPresenter(view: MessageThreadsPresenter.View): MessageThreadsPresenter {
        return MessageThreadsPresenter(view, provideMessageService())
    }

    fun provideImagePresenter(view: ImagePresenter.View): ImagePresenter {
        return ImagePresenter(view, providePostService())
    }

    fun provideVideoPresenter(view: VideoPresenter.View): VideoPresenter {
        return VideoPresenter(view, providePostService())
    }

    // ==========================================
    // Services
    // ==========================================

    private fun provideTagService(): TagService {
        return TagService(
            resolveDataContextFactory(),
            PostsForTagRequest(),
            PostMerger(resolveDataContextFactory()))
    }

    private fun providePostService(): PostService {
        return PostService(
            provideImageRequestFactory(),
            resolvePostRequest(),
            resolveMemoryBuffer(),
            resolveDataContextFactory())
    }

    private fun provideTagsService(): TagListService {
        return TagListService(
            resolveDataContextFactory(),
            provideMyTagSynchronizer())
    }

    private fun provideCommentService(): CommentService {
        return CommentService(
            provideCreateCommentRequestFactory(),
            resolvePostRequest(),
            resolveMemoryBuffer())
    }

    private fun provideProfileService(): ProfileService {
        return ProfileService(
            provideProfileRequestFactory(),
            provideLoginRequestFactory())
    }

    private fun provideMessageService(): MessageService {
        return MessageService(
            providePrivateMessageSynchronizer(),
            resolveMemoryBuffer())
    }

    // ==========================================
    // Models
    // ==========================================

    private fun resolvePostRequest(): PostRequest {
        return PostRequest()
    }

    private fun resolveMemoryBuffer(): MemoryBuffer {
        return MemoryBuffer
    }

    private fun provideImageRequestFactory(): OriginalImageRequestFactory {
        return OriginalImageRequestFactory()
    }

    private fun provideMyTagSynchronizer(): MyTagFetcher {
        return MyTagFetcher(resolveDataContextFactory())
    }

    private fun provideLoginRequestFactory(): LoginRequestFactory {
        return LoginRequestFactory()
    }

    private fun provideProfileRequestFactory(): ProfileRequestFactory {
        return ProfileRequestFactory()
    }

    private fun provideCreateCommentRequestFactory(): CreateCommentRequestFactory {
        return CreateCommentRequestFactory()
    }

    private fun providePrivateMessageSynchronizer(): PrivateMessageFetcher {
        return PrivateMessageFetcher(
            provideMessageListRequest(),
            resolveMemoryBuffer())
    }

    private fun provideMessageListRequest(): MessageListRequest {
        return MessageListRequest(UserImageRequest())
    }

    private fun resolveDataContextFactory(): DataContext.Factory {
        return DataContext.Factory()
    }
}
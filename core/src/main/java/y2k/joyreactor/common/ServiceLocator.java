package y2k.joyreactor.common;

import y2k.joyreactor.presenters.*;
import y2k.joyreactor.services.*;
import y2k.joyreactor.services.repository.DataContext;
import y2k.joyreactor.services.requests.*;
import y2k.joyreactor.services.synchronizers.MyTagFetcher;
import y2k.joyreactor.services.synchronizers.PostListFetcher;
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher;

/**
 * Created by y2k on 07/12/15.
 */
public class ServiceLocator {

    private static ServiceLocator sInstance = new ServiceLocator();

    public static ServiceLocator getInstance() {
        return sInstance;
    }

    // ==========================================
    // Presenters
    // ==========================================

    public PostListPresenter providePostListPresenter(PostListPresenter.View view) {
        return new PostListPresenter(view, provideTagService());
    }

    public PostPresenter providePostPresenter(PostPresenter.View view) {
        return new PostPresenter(view, providePostService());
    }

    public TagListPresenter provideTagListPresenter(TagListPresenter.View view) {
        return new TagListPresenter(view, provideTagsService());
    }

    public ProfilePresenter provideProfilePresenter(ProfilePresenter.View view) {
        return new ProfilePresenter(view, provideProfileService());
    }

    public CreateCommentPresenter provideCreateCommentPresenter(CreateCommentPresenter.View view) {
        return new CreateCommentPresenter(view, provideProfileService(), provideCommentService());
    }

    public LoginPresenter provideLoginPresenter(LoginPresenter.View view) {
        return new LoginPresenter(view, provideProfileService());
    }

    public AddTagPresenter provideAddTagPresenter(AddTagPresenter.View view) {
        return new AddTagPresenter(view, provideTagsService());
    }

    public MessagesPresenter provideMessagesPresenter(MessagesPresenter.View view) {
        return new MessagesPresenter(view, provideMessageService());
    }

    public MessageThreadsPresenter provideMessageThreadsPresenter(MessageThreadsPresenter.View view) {
        return new MessageThreadsPresenter(view, provideMessageService());
    }

    public ImagePresenter provideImagePresenter(ImagePresenter.View view) {
        return new ImagePresenter(view, providePostService());
    }

    public VideoPresenter provideVideoPresenter(VideoPresenter.View view) {
        return new VideoPresenter(view, providePostService());
    }

    // ==========================================
    // Services
    // ==========================================

    private TagService provideTagService() {
        return new TagService(
                resolveDataContextFactory(),
                providePostListSynchronizerFactory());
    }

    private PostService providePostService() {
        return new PostService(
                provideImageRequestFactory(),
                resolvePostRequest(),
                resolveMemoryBuffer());
    }

    private TagListService provideTagsService() {
        return new TagListService(
                resolveDataContextFactory(),
                provideMyTagSynchronizer());
    }

    private CommentService provideCommentService() {
        return new CommentService(
                provideCreateCommentRequestFactory(),
                resolvePostRequest(),
                resolveMemoryBuffer());
    }

    private ProfileService provideProfileService() {
        return new ProfileService(
                provideProfileRequestFactory(),
                provideLoginRequestFactory());
    }

    private MessageService provideMessageService() {
        return new MessageService(
                providePrivateMessageSynchronizer(),
                resolveMemoryBuffer());
    }

    // ==========================================
    // Models
    // ==========================================

    private PostRequest resolvePostRequest() {
        return new PostRequest();
    }

    private MemoryBuffer resolveMemoryBuffer() {
        return MemoryBuffer.INSTANCE;
    }

    private OriginalImageRequestFactory provideImageRequestFactory() {
        return new OriginalImageRequestFactory();
    }

    private PostListFetcher.Factory providePostListSynchronizerFactory() {
        return new PostListFetcher.Factory(resolveDataContextFactory());
    }

    private MyTagFetcher provideMyTagSynchronizer() {
        return new MyTagFetcher(resolveDataContextFactory());
    }

    private LoginRequestFactory provideLoginRequestFactory() {
        return new LoginRequestFactory();
    }

    private ProfileRequestFactory provideProfileRequestFactory() {
        return new ProfileRequestFactory();
    }

    private CreateCommentRequestFactory provideCreateCommentRequestFactory() {
        return new CreateCommentRequestFactory();
    }

    private PrivateMessageFetcher providePrivateMessageSynchronizer() {
        return new PrivateMessageFetcher(
                provideMessageListRequest(),
                resolveMemoryBuffer());
    }

    private MessageListRequest provideMessageListRequest() {
        return new MessageListRequest();
    }

    private DataContext.Factory resolveDataContextFactory() {
        return new DataContext.Factory();
    }
}
package y2k.joyreactor.common;

import y2k.joyreactor.*;
import y2k.joyreactor.presenters.*;
import y2k.joyreactor.services.*;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.*;
import y2k.joyreactor.services.synchronizers.MyTagFetcher;
import y2k.joyreactor.services.synchronizers.PostListFetcher;
import y2k.joyreactor.services.synchronizers.PostFetcher;
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher;

/**
 * Created by y2k on 07/12/15.
 */
// TODO: переимновать в ServiceLocator
public class DependencyInjection {

    private static DependencyInjection sInstance = new DependencyInjection();

    public static DependencyInjection getInstance() {
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
        return new TagService(provideRepository(Post.class), providePostListSynchronizerFactory());
    }

    private PostService providePostService() {
        return new PostService(
                providePostSynchronizer(),
                provideRepository(Post.class),
                provideRepository(Comment.class),
                provideRepository(SimilarPost.class),
                provideRepository(Attachment.class),
                provideImageRequestFactory());
    }

    private TagsService provideTagsService() {
        return new TagsService(provideRepository(Tag.class), provideMyTagSynchronizer());
    }

    private CommentService provideCommentService() {
        return new CommentService(provideCreateCommentRequestFactory(), providePostSynchronizer());
    }

    private ProfileService provideProfileService() {
        return new ProfileService(provideProfileRequestFactory(), provideLoginRequestFactory());
    }

    private MessageService provideMessageService() {
        return new MessageService(providePrivateMessageSynchronizer(), provideRepositoryMessage());
    }

    // ==========================================
    // Models
    // ==========================================

    private OriginalImageRequestFactory provideImageRequestFactory() {
        return new OriginalImageRequestFactory();
    }

    private PostListFetcher.Factory providePostListSynchronizerFactory() {
        return new PostListFetcher.Factory(provideRepository(Post.class), provideRepositoryTagPost());
    }

    private Repository<TagPost> provideRepositoryTagPost() {
        return new Repository<>(TagPost.class);
    }

    private PostFetcher providePostSynchronizer() {
        return new PostFetcher(
                provideRepository(SimilarPost.class),
                provideRepository(Attachment.class),
                provideRepository(Post.class),
                provideRepository(Comment.class));
    }

    private MyTagFetcher provideMyTagSynchronizer() {
        return new MyTagFetcher(provideRepository(Tag.class));
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

    private Repository<Message> provideRepositoryMessage() {
        return new Repository<>(Message.class);
    }

    private PrivateMessageFetcher providePrivateMessageSynchronizer() {
        return new PrivateMessageFetcher(
                provideMessageListRequest(),
                provideRepository(Message.class));
    }

    private <T> Repository<T> provideRepository(Class<T> aClass) {
        return new Repository<>(aClass);
    }

    private MessageListRequest provideMessageListRequest() {
        return new MessageListRequest();
    }
}
package y2k.joyreactor.common;

import y2k.joyreactor.*;
import y2k.joyreactor.presenters.*;
import y2k.joyreactor.services.*;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.CreateCommentRequestFactory;
import y2k.joyreactor.services.requests.LoginRequestFactory;
import y2k.joyreactor.services.requests.OriginalImageRequestFactory;
import y2k.joyreactor.services.requests.ProfileRequestFactory;
import y2k.joyreactor.services.synchronizers.MyTagFetcher;
import y2k.joyreactor.services.synchronizers.PostListFetcher;
import y2k.joyreactor.services.synchronizers.PostFetcher;
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher;

/**
 * Created by y2k on 07/12/15.
 */
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

    // ==========================================
    // Services
    // ==========================================

    public TagService provideTagService() {
        return new TagService(providePostRepository(), providePostListSynchronizerFactory());
    }

    public PostService providePostService() {
        return new PostService(
                providePostSynchronizer(), providePostRepository(),
                provideCommentRepository(),
                provideSimilarPostRepository(),
                provideAttachmentRepository(),
                provideImageRequestFactory());
    }

    public TagsService provideTagsService() {
        return new TagsService(provideRepositoryTag(), provideMyTagSynchronizer());
    }

    public CommentService provideCommentService() {
        return new CommentService(provideCreateCommentRequestFactory(), providePostSynchronizer());
    }

    private ProfileService provideProfileService() {
        return new ProfileService(provideProfileRequestFactory(), provideLoginRequestFactory());
    }

    public MessageService provideMessageService() {
        return new MessageService(providePrivateMessageSynchronizer(), provideRepositoryMessage());
    }

    // ==========================================
    // Models
    // ==========================================

    private OriginalImageRequestFactory provideImageRequestFactory() {
        return new OriginalImageRequestFactory();
    }

    public PostListFetcher.Factory providePostListSynchronizerFactory() {
        return new PostListFetcher.Factory(providePostRepository(), provideRepositoryTagPost());
    }

    public Repository<TagPost> provideRepositoryTagPost() {
        return new Repository<>(TagPost.class);
    }

    public PostFetcher providePostSynchronizer() {
        return new PostFetcher(provideSimilarPostRepository(), provideAttachmentRepository());
    }

    public Repository<Attachment> provideAttachmentRepository() {
        return new Repository<>(Attachment.class);
    }

    public Repository<SimilarPost> provideSimilarPostRepository() {
        return new Repository<>(SimilarPost.class);
    }

    public Repository<Comment> provideCommentRepository() {
        return new Repository<>(Comment.class);
    }

    public Repository<Post> providePostRepository() {
        return new Repository<>(Post.class);
    }

    public MyTagFetcher provideMyTagSynchronizer() {
        return new MyTagFetcher(provideRepositoryTag());
    }

    public Repository<Tag> provideRepositoryTag() {
        return new Repository<>(Tag.class);
    }

    public LoginRequestFactory provideLoginRequestFactory() {
        return new LoginRequestFactory();
    }

    public ProfileRequestFactory provideProfileRequestFactory() {
        return new ProfileRequestFactory();
    }

    public CreateCommentRequestFactory provideCreateCommentRequestFactory() {
        return new CreateCommentRequestFactory();
    }

    private Repository<Message> provideRepositoryMessage() {
        return new Repository<>(Message.class);
    }

    private PrivateMessageFetcher providePrivateMessageSynchronizer() {
        return new PrivateMessageFetcher();
    }
}
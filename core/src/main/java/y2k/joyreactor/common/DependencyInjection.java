package y2k.joyreactor.common;

import y2k.joyreactor.*;
import y2k.joyreactor.presenters.PostListPresenter;
import y2k.joyreactor.presenters.PostPresenter;
import y2k.joyreactor.services.PostService;
import y2k.joyreactor.services.TagService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.PostListSynchronizer;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;

/**
 * Created by y2k on 07/12/15.
 */
public class DependencyInjection {

    private static DependencyInjection sInstance = new DependencyInjection();

    public static DependencyInjection getInstance() {
        return sInstance;
    }

    public PostListPresenter providePostListPresenter(PostListPresenter.View view) {
        return new PostListPresenter(view, provideTagService());
    }

    public TagService provideTagService() {
        return new TagService(providePostRepository(), providePostListSynchronizerFactory());
    }

    public PostPresenter providePostPresenter(PostPresenter.View view) {
        return new PostPresenter(view, providePostService());
    }

    public PostListSynchronizer.Factory providePostListSynchronizerFactory() {
        return new PostListSynchronizer.Factory(providePostRepository(), provideRepositoryTagPost());
    }

    public Repository<TagPost> provideRepositoryTagPost() {
        return new Repository<>(TagPost.class);
    }

    public PostService providePostService() {
        return new PostService(
                providePostRepository(),
                providePostSynchronizer(),
                provideCommentRepository(),
                provideSimilarPostRepository(),
                provideAttachmentRepository()
        );
    }

    public PostSynchronizer providePostSynchronizer() {
        return new PostSynchronizer(provideSimilarPostRepository(), provideAttachmentRepository());
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
}
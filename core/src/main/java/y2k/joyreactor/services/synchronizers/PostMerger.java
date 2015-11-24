package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPost;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.services.repository.PostByIdQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.repository.TagPostsForTagQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger {

    private PostSubRepositoryForTag repository;
    private Repository<Post> postRepository;
    private Repository<TagPost> tagPostRepository;
    private Tag tag;

    private Integer divider;

    PostMerger(PostSubRepositoryForTag repository, Tag tag, Repository<Post> postRepository, Repository<TagPost> tagPostRepository) {
        this.repository = repository;
        this.tag = tag;
        this.postRepository = postRepository;
        this.tagPostRepository = tagPostRepository;
    }

    public Integer getDivider() {
        return divider;
    }

    public Observable<Void> mergeFirstPage(List<Post> newPosts) {
        return updatePostsAsync(newPosts).flatMap(_void -> {
            return tagPostRepository
                    .queryAsync(new TagPostsForTagQuery(tag))
                    .map(links -> {
                        List<TagPost> merged = new ArrayList<>(links);
                        for (Iterator<TagPost> iterator = merged.iterator(); iterator.hasNext(); ) {
                            TagPost element = iterator.next();
                            for (Post s : newPosts)
                                if (s.id == element.postId) {
                                    iterator.remove();
                                    break;
                                }
                        }

                        for (int i = 0; i < newPosts.size(); i++)
                            merged.add(i, new TagPost(tag.id, newPosts.get(i).id));

                        divider = newPosts.size();
                        return merged;
                    })
                    .flatMap(s -> tagPostRepository.replaceAllAsync(new TagPostsForTagQuery(tag), s));
        });
    }

    public Observable<Boolean> isUnsafeUpdate(List<Post> newPosts) {
        return repository
                .queryAsync()
                .map(posts -> {
                    if (posts.size() == 0) return false;
                    if (newPosts.size() > posts.size()) return true;
                    for (int i = 0; i < newPosts.size(); i++)
                        if (!posts.get(i).serverId.equals(newPosts.get(i).serverId)) return true;
                    return false;
                });
    }

    public Observable<Void> mergeNextPage(List<Post> newPosts) {
        return updatePostsAsync(newPosts).flatMap(_void -> {
            return tagPostRepository
                    .queryAsync(new TagPostsForTagQuery(tag))
                    .map(posts -> {
                        List<TagPost> actualPosts = posts.subList(0, divider);
                        List<TagPost> expiredPosts = new ArrayList<>(posts.subList(divider, posts.size()));

                        for (Post p : newPosts) {
                            addIfNew(actualPosts, p);
                            remove(expiredPosts, p);
                        }
                        divider = actualPosts.size();
                        return union(actualPosts, expiredPosts);

                    })
                    .flatMap(s -> tagPostRepository.replaceAllAsync(new TagPostsForTagQuery(tag), s));
        });
    }

    private Observable<Void> updatePostsAsync(List<Post> newPosts) {
        return ObservableUtils.create(() -> {
            for (Post p : newPosts) {
                postRepository.insertOrUpdate(new PostByIdQuery(p.serverId), p);
            }
        });
    }

    private void addIfNew(List<TagPost> list, Post item) {
        for (TagPost s : list)
            if (s.postId == item.id) return;
        list.add(new TagPost(tag.id, item.id));
    }

    private void remove(List<TagPost> list, Post item) {
        for (Iterator<TagPost> iterator = list.iterator(); iterator.hasNext(); )
            if (iterator.next().postId == item.id) iterator.remove();
    }

    private List<TagPost> union(List<TagPost> left, List<TagPost> right) {
        left.addAll(right);
        return left;
    }
}
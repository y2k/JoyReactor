package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPost;
import y2k.joyreactor.common.ObjectUtils;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.services.repository.PostByIdQuery;
import y2k.joyreactor.services.repository.PostsForTagQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.repository.TagPostsForTagQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger {

    private Repository<Post> postRepository;
    private Repository<TagPost> tagPostRepository;
    private Tag tag;

    private Integer divider;

    PostMerger(Tag tag, Repository<Post> postRepository, Repository<TagPost> tagPostRepository) {
        this.tag = tag;
        this.postRepository = postRepository;
        this.tagPostRepository = tagPostRepository;
    }

    public Integer getDivider() {
        return divider;
    }

    public Observable<Void> mergeFirstPage(List<Post> newPosts) {
        return updatePostsAsync(newPosts)
                .flatMap(_void -> {
                    return tagPostRepository
                            .queryAsync(new TagPostsForTagQuery(tag))
                            .map(links -> {
                                List<TagPost> result = new ArrayList<>();

                                for (Post s : newPosts)
                                    result.add(new TagPost(tag.id, s.getId()));
                                for (TagPost s : links)
                                    if (!contains(result, s))
                                        result.add(s);

                                divider = newPosts.size();
                                return result;
                            })
                            .flatMap(s -> tagPostRepository.replaceAllAsync(new TagPostsForTagQuery(tag), s));
                });
    }

    private boolean contains(List<TagPost> list, TagPost tagPost) {
        for (TagPost s : list)
            if (s.getPostId() == tagPost.getPostId()) return true;
        return false;
    }

    public Observable<Boolean> isUnsafeUpdate(List<Post> newPosts) {
        return postRepository
                .queryAsync(new PostsForTagQuery(tag))
                .map(oldPosts -> {
                    if (oldPosts.size() == 0) return false;
                    if (newPosts.size() > oldPosts.size()) return true;
                    for (int i = 0; i < newPosts.size(); i++) {
                        String oldId = oldPosts.get(i).getServerId();
                        String newId = newPosts.get(i).getServerId();
                        if (!ObjectUtils.equals(oldId, newId)) return true;
                    }
                    return false;
                });
    }

    public Observable<Void> mergeNextPage(List<Post> newPosts) {
        return updatePostsAsync(newPosts)
                .flatMap(_void -> {
                    return tagPostRepository
                            .queryAsync(new TagPostsForTagQuery(tag))
                            .map(links -> {
                                List<TagPost> actualPosts = links.subList(0, divider);
                                List<TagPost> expiredPosts = new ArrayList<>(links.subList(divider, links.size()));

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
                postRepository.insertOrUpdate(new PostByIdQuery(p.getServerId()), p);
            }
        });
    }

    private void addIfNew(List<TagPost> list, Post item) {
        for (TagPost s : list)
            if (s.getPostId() == item.getId()) return;
        list.add(new TagPost(tag.id, item.getId()));
    }

    private void remove(List<TagPost> list, Post item) {
        for (Iterator<TagPost> iterator = list.iterator(); iterator.hasNext(); )
            if (iterator.next().getPostId() == item.getId()) iterator.remove();
    }

    private List<TagPost> union(List<TagPost> left, List<TagPost> right) {
        left.addAll(right);
        return left;
    }
}
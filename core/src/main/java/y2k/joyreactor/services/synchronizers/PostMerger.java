package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.services.repository.PostSubRepositoryForTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger {

    private PostSubRepositoryForTag repository;
    private Integer divider;

    PostMerger(PostSubRepositoryForTag repository) {
        this.repository = repository;
    }

    public Integer getDivider() {
        return divider;
    }

    public Observable<Void> mergeFirstPage(List<Post> newPosts) {
        return repository
                .queryAsync()
                .map(posts -> {
                    ArrayList<Post> merged = new ArrayList<>(posts);
                    for (Iterator<Post> iterator = merged.iterator(); iterator.hasNext(); ) {
                        Post element = iterator.next();
                        for (Post s : newPosts)
                            if (s.serverId.equals(element.serverId)) {
                                iterator.remove();
                                break;
                            }
                    }
                    merged.addAll(0, newPosts);

                    divider = newPosts.size();
                    return merged;
                })
                .flatMap(repository::replaceAllAsync);
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
        return repository
                .queryAsync()
                .map(posts -> {
                    List<Post> actualPosts = posts.subList(0, divider);
                    List<Post> expiredPosts = new ArrayList<>(posts.subList(divider, posts.size()));

                    for (Post p : newPosts) {
                        addIfNew(actualPosts, p);
                        remove(expiredPosts, p);
                    }
                    divider = actualPosts.size();
                    return union(actualPosts, expiredPosts);
                })
                .flatMap(repository::replaceAllAsync);
    }

    private void remove(List<Post> list, Post item) {
        for (Iterator<Post> iterator = list.iterator(); iterator.hasNext(); )
            if (iterator.next().serverId.equals(item.serverId)) iterator.remove();
    }

    private void addIfNew(List<Post> list, Post item) {
        for (Post s : list)
            if (s.serverId.equals(item.serverId)) return;
        list.add(item);
    }

    private List<Post> union(List<Post> left, List<Post> right) {
        left.addAll(right);
        return left;
    }
}
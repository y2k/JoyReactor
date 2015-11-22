package y2k.joyreactor.repository;

import y2k.joyreactor.Post;

/**
 * Created by y2k on 11/21/15.
 */
public class PostByIdQuery extends Repository.Query<Post> {

    private String postId;

    public PostByIdQuery(String postId) {
        this.postId = postId;
    }

    @Override
    public boolean compare(Post post) {
        return postId.equals(post.serverId);
    }
}
package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Attachment
import y2k.joyreactor.Comment
import y2k.joyreactor.Post
import y2k.joyreactor.SimilarPost
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.services.repository.*
import y2k.joyreactor.services.requests.PostRequest

/**
 * Created by y2k on 11/21/15.
 */
class PostFetcher(private val similarPostRepository: Repository<SimilarPost>,
                  private val attachmentRepository: Repository<Attachment>,
                  private val postRepository: Repository<Post>,
                  private val commentRepository: Repository<Comment>) {

    private val postRequest = PostRequest()

    fun synchronizeWithWeb(postId: String): Observable<Void> {
        return ObservableUtils.action {
            postRequest.request(postId)

            val post = postRequest.post!!
            postRepository.insertOrUpdate(PostByIdQuery(post.serverId), post)

            saveComments(post)
            saveSimilarPosts(post)
            saveAttachments(post.id)
        }
    }

    private fun saveAttachments(postId: Int) {
        val attachments = postRequest.getAttachments()
        for (a in attachments)
            a.postId = postId

        attachmentRepository.deleteWhere(AttachmentsQuery(postId))
        attachmentRepository.insertAll(attachments)
    }

    private fun saveComments(post: Post) {
        val comments = postRequest.comments
        for (c in comments)
            c.postId = post.id

        commentRepository.deleteWhere(CommentsForPostQuery(post.id))
        commentRepository.insertAll(comments)
    }

    private fun saveSimilarPosts(post: Post) {
        val posts = postRequest.getSimilarPosts()
        for (s in posts)
            s.parentPostId = post.id

        similarPostRepository.deleteWhere(SimilarPostQuery(post.id))
        similarPostRepository.insertAll(posts)
    }
}
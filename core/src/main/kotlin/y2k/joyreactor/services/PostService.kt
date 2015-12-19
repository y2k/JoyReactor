package y2k.joyreactor.services

import rx.Observable
import rx.functions.Func1
import y2k.joyreactor.*
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.services.repository.*
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.synchronizers.PostFetcher

import java.io.File
import java.util.ArrayList

/**
 * Created by y2k on 11/24/15.
 */
class PostService(private val synchronizer: PostFetcher,
                  private val repository: Repository<Post>,
                  private val commentRepository: Repository<Comment>,
                  private val similarPostRepository: Repository<SimilarPost>,
                  private val attachmentRepository: Repository<Attachment>,
                  private val imageRequestFactory: OriginalImageRequestFactory) {

    fun synchronizePostAsync(postId: String): Observable<Post> {
        return synchronizer
                .synchronizeWithWeb(postId)
                .flatMap({ repository.queryFirstAsync(PostByIdQuery(postId)) })
    }

    fun getCommentsAsync(postId: Int, parentCommentId: Int): Observable<CommentGroup> {
        if (parentCommentId == 0)
            return getCommentForPost(postId)
        return commentRepository
                .queryFirstByIdAsync(parentCommentId)
                .flatMap({ parent ->
                    commentRepository
                            .queryAsync(CommentsForPostQuery(postId, parentCommentId))
                            .map({ children -> CommentGroup.OneLevel(parent, children) })
                })
    }

    private fun getCommentForPost(postId: Int): Observable<CommentGroup> {
        return commentRepository
                .queryAsync(TwoLeverCommentQuery(postId))
                .map<CommentGroup>(Func1<List<Comment>, CommentGroup> { CommentGroup.TwoLevel(it) })
    }

    fun getFromCache(postId: String): Observable<Post> {
        return repository.queryFirstAsync(PostByIdQuery(postId))
    }

    fun getPostImages(postId: Int): Observable<List<Image>> {
        val postAttachments = attachmentRepository
                .queryAsync(AttachmentsQuery(postId))
                .flatMap({ attachments -> Observable.from(attachments).map({ s -> s.image }).toList() })

        val commentAttachments = commentRepository
                .queryAsync(CommentsWithImagesQuery(postId, 10))
                .flatMap({ comments ->
                    Observable
                            .from<Comment>(comments)
                            .map({ it.getAttachment() })
                            .toList()
                })

        return postAttachments.flatMap({ s -> commentAttachments.map({ s2 -> union(s, s2) }) })
    }

    private fun union(s: List<Image>, s2: List<Image>): List<Image> {
        val result = ArrayList(s)
        result.addAll(s2)
        return result
    }

    fun getTopComments(postId: Int, maxCount: Int): Observable<CommentGroup> {
        return commentRepository
                .queryAsync(TopCommentsQuery(postId, maxCount))
                .map<CommentGroup>(Func1<List<Comment>, CommentGroup> { CommentGroup.OneLevel(it) })
    }

    fun getSimilarPosts(postId: Int): Observable<List<SimilarPost>> {
        return similarPostRepository.queryAsync(SimilarPostQuery(postId))
    }

    fun mainImage(serverPostId: String): Observable<File> {
        return repository
                .queryFirstAsync(PostByIdQuery(serverPostId))
                .map({ post -> post.image.fullUrl(null) })
                .flatMap({ url -> imageRequestFactory.request(url) })
    }

    fun mainImagePartial(serverPostId: String): Observable<PartialResult<File>> {
        return repository
                .queryFirstAsync(PostByIdQuery(serverPostId))
                .map({ post -> post.image.fullUrl(null) })
                .flatMap({ url -> imageRequestFactory.requestPartial(url) })
    }
}
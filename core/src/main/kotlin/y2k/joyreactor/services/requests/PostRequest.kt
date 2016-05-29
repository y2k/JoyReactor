package y2k.joyreactor.services.requests

import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.*
import y2k.joyreactor.services.requests.parser.PostParser
import java.util.*
import java.util.regex.Pattern

/**
 * Created by y2k on 11/21/15.
 */
class PostRequest(
    private val httpClient: HttpClient,
    private val parser: PostParser) {

    var post: Post? = null
        private set

    private val commentsRequest = PostCommentsRequest()
    private val similarPosts = ArrayList<SimilarPost>()
    private val attachments = ArrayList<Attachment>()

    fun getSimilarPosts(): List<SimilarPost> {
        return similarPosts
    }

    val comments: List<Comment>
        get() = commentsRequest.comments

    fun request(postId: String) {
        val page = httpClient.getDocument(getPostUrl(postId))

        val postNode = page.select("div.postContainer").first()
        post = parser.parse(postNode)

        commentsRequest.request(page, postId.toLong())

        for (e in page.select(".similar_post img")) {
            val similarPost = SimilarPost(getPostId(e.parent().attr("href")))
            similarPost.image = Image(e.absUrl("src"), 0, 0)
            similarPosts.add(similarPost)
        }

        val imgElement = postNode.select("div.image > img")
        if (imgElement.size > 1)
            for (e in imgElement.subList(1, imgElement.size - 1)) {
                val a = Attachment(Image(e.absUrl("src"),
                    Integer.parseInt(e.attr("width")),
                    Integer.parseInt(e.attr("height"))))
                attachments.add(a)
            }
    }

    private fun getPostId(href: String): String {
        val m = POST_ID.matcher(href)
        if (!m.find()) throw IllegalStateException()
        return m.group(1)
    }

    private fun getPostUrl(postId: String): String {
        return "http://anime.reactor.cc/post/" + postId // TODO:
    }

    fun getAttachments(): List<Attachment> {
        return attachments
    }

    companion object {

        private val POST_ID = Pattern.compile("/post/(\\d+)")
    }
}
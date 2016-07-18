package y2k.joyreactor.services.requests

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.postAsync
import java.util.regex.Pattern

/**
 * Created by y2k on 19/10/15.
 */
class CreateCommentRequest(
    private val httpClient: HttpClient) : Function2<Long, String, CompletableFuture<*>> {

    private val commentId: String? = null

    override fun invoke(postId: Long, commentText: String): CompletableFuture<*> {
        return httpClient
            .buildRequest()
            .addField("parent_id", commentId ?: "0")
            .addField("post_id", "" + postId)
            .addField("token", getToken())
            .addField("comment_text", commentText)
            .putHeader("X-Requested-With", "XMLHttpRequest")
            .putHeader("Referer", "http://joyreactor.cc/post/" + postId)
            .postAsync("http://joyreactor.cc/post_comment/create")
    }

    private fun getToken(): String {
        val document = httpClient.getText("http://joyreactor.cc/donate")
        val m = TOKEN_REGEX.matcher(document)
        if (!m.find()) throw IllegalStateException()
        return m.group(1)
    }

    companion object {

        val TOKEN_REGEX = Pattern.compile("var token = '(.+?)'")
    }
}
package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient

import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by y2k on 19/10/15.
 */
class CreateCommentRequestFactory {

    private val commentId: String? = null

    fun create(postId: String, commentText: String): Observable<Void> {
        return ObservableUtils.action {
            HttpClient.getInstance()
                    .beginForm()
                    .put("parent_id", commentId ?: "0")
                    .put("post_id", postId)
                    .put("token", getToken())
                    .put("comment_text", commentText)
                    .putHeader("X-Requested-With", "XMLHttpRequest")
                    .putHeader("Referer", "http://joyreactor.cc/post/" + postId)
                    .send("http://joyreactor.cc/post_comment/create")
        }
    }

    private fun getToken(): String {
        val document = HttpClient.getInstance().getText("http://joyreactor.cc/donate")
        val m = TOKEN_REGEX.matcher(document)
        if (!m.find()) throw IllegalStateException()
        return m.group(1)
    }

    companion object {

        val TOKEN_REGEX = Pattern.compile("var token = '(.+?)'")
    }
}

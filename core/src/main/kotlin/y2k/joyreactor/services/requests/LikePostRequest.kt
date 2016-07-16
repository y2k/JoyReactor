package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import y2k.joyreactor.common.ajax
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.getAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.MyLike

/**
 * Created by y2k on 4/24/16.
 */
/**
 * http://joyreactor.cc/post_vote/add/2596695/plus?token=9448cab044c0ce6f24b4ffb7e828b9f6&abyss=0
 * http://joyreactor.cc/post_vote/add/2596695/minus?token=9448cab044c0ce6f24b4ffb7e828b9f6&abyss=0
 *
 * <span>8.2<div class="vote-plus vote-change"></div>  <div class="vote-minus "></div></span>
 * <span>8.8<div class="vote-plus "></div>  <div class="vote-minus vote-change"></div></span>
 */
class LikePostRequest(
    private val httpClient: HttpClient,
    private val requestToken: () -> CompletableContinuation<String>,
    private val parseLike: (Element) -> MyLike) {

    operator fun invoke(id: Long, like: Boolean): CompletableContinuation<Pair<Float, MyLike>> {
        return requestToken()
            .then { token -> createUrl(id, token, like) }
            .thenAsync {
                httpClient
                    .buildRequest()
                    .ajax("http://joyreactor.cc/")
                    .getAsync(it)
            }
            .then {
                Pair(getNewRating(it), parseLike(it.body()))
            }
    }

    private fun createUrl(id: Long, token: String, like: Boolean) =
        "http://joyreactor.cc/post_vote/add/$id/${action(like)}?token=$token&abyss=0"

    private fun action(like: Boolean) = if (like) "plus" else "minus"

    private fun getNewRating(likeResponse: Document): Float {
        return likeResponse.select("span").first().childNodes()[0].outerHtml().toFloat()
    }
}
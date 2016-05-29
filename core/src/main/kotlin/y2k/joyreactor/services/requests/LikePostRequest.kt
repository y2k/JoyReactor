package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import rx.Observable
import y2k.joyreactor.common.ajax
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.MyLike
import y2k.joyreactor.services.requests.parser.LikeParser

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
    private val tokenRequest: TokenRequest) {

    fun like(id: Long, like: Boolean): Observable<Pair<Float, MyLike>> {
        return tokenRequest
            .request()
            .map {
                val url = "http://joyreactor.cc/post_vote/add/$id/${action(like)}?token=$it&abyss=0"
                val likeResponse = httpClient
                    .buildRequest()
                    .ajax("http://joyreactor.cc/")
                    .get(url)

                val rating = getNewRating(likeResponse)
                val myLike = getMyLike(likeResponse)
                rating to myLike
            }
    }

    private fun action(like: Boolean) = if (like) "plus" else "minus"

    private fun getNewRating(likeResponse: Document): Float {
        return likeResponse.select("span").first().childNodes()[0].outerHtml().toFloat()
    }

    private fun getMyLike(document: Document): MyLike {
        return LikeParser(document.body()).myLike
    }
}
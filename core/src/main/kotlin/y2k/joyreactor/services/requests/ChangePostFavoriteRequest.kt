package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import rx.Observable
import y2k.joyreactor.common.ajax
import y2k.joyreactor.common.http.HttpClient

/**
 * Created by y2k on 5/28/2016.
 */
class ChangePostFavoriteRequest(
    val httpClient: HttpClient,
    val tokenRequest: TokenRequest) {

    // http://joyreactor.cc/favorite/create/2646561?token=aa2e06a6265aad3ff2ec17b287bee4fa&rand=7412
    // http://joyreactor.cc/favorite/delete/2646561?token=aa2e06a6265aad3ff2ec17b287bee4fa&rand=5241

    fun execute(postId: Long, favorite: Boolean): Observable<Document> {
        return tokenRequest
            .request()
            .map { token ->
                val action = if (favorite) "create" else "delete"
                httpClient
                    .buildRequest()
                    .ajax("http://joyreactor.cc/")
                    .get("http://joyreactor.cc/favorite/$action/$postId?token=$token&rand=${Math.random()}")
            }
    }
}
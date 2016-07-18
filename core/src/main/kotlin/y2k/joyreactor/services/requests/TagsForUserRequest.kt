package y2k.joyreactor.services.requests

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Image
import java.util.*

/**
 * Created by y2k on 19/10/15.
 */
class TagsForUserRequest(
    private val httpClient: HttpClient,
    val platform: Platform) {

    private val imageRequest = TagImageRequest(httpClient, platform)

    fun request(username: String): CompletableFuture<List<Group>> {
        return runAsync {
            val document = httpClient.getDocument("http://joyreactor.cc/user/" + username)
            val tags = ArrayList<Group>()
            for (h in document.select(".sideheader")) {
                if ("Читает" == h.text()) {
                    for (a in h.parent().select("a")) {
                        val name = a.text()
                        tags.add(Group.makeTag(name, Image(imageRequest.request(name))))
                    }
                    break
                }
            }
            tags
        }
    }
}
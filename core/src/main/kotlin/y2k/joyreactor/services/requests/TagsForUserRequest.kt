package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Tag
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.http.HttpClient
import java.util.*

/**
 * Created by y2k on 19/10/15.
 */
class TagsForUserRequest(private val username: String) {

    private val imageRequest = TagImageRequest()

    fun request(): Observable<List<Tag>> {
        return ioObservable {
            val document = HttpClient.instance.getDocument("http://joyreactor.cc/user/" + username)
            val tags = ArrayList<Tag>()
            for (h in document.select(".sideheader")) {
                if ("Читает" == h.text()) {
                    for (a in h.parent().select("a")) {
                        val name = a.text()
                        tags.add(Tag(name, name, false, Image(imageRequest.request(name))))
                    }
                    break
                }
            }
            tags
        }
    }
}
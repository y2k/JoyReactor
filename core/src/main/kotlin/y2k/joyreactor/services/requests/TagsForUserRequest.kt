package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.Image
import y2k.joyreactor.Tag
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient
import java.util.*

/**
 * Created by y2k on 19/10/15.
 */
class TagsForUserRequest(private val username: String) {

    private val imageRequest = TagImageRequest()

    fun request(): Observable<List<Tag>> {
        return ObservableUtils.func <List<Tag>> {
            val document = HttpClient.instance
                    .getDocument("http://joyreactor.cc/user/" + username)
            val tags = ArrayList<Tag>()
            for (h in document.select(".sideheader")) {
                if ("Читает" == h.text()) {
                    for (a in h.parent().select("a"))
                        tags.add(createTag(a.text()))
                    break
                }
            }
            Collections.sort(tags) { l, r -> l.title!!.compareTo(r.title!!, ignoreCase = true) }
            tags
        }
    }

    private fun createTag(title: String): Tag {
        val tag = Tag()
        tag.title = title
        tag.image = Image(imageRequest.request(title), 0, 0)
        return tag
    }
}
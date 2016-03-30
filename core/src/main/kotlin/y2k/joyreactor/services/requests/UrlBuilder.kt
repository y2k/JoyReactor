package y2k.joyreactor.services.requests

import y2k.joyreactor.model.Group

import java.net.URLEncoder

/**
 * Created by y2k on 11/8/15.
 */
internal class UrlBuilder {

    fun build(group: Group, pageId: String?): String {
        val url = StringBuilder("http://joyreactor.cc/")
        when (group.type) {
            Group.Type.User -> url.append("user/" + URLEncoder.encode(group.name))
            Group.Type.Tag -> url.append("tag/" + URLEncoder.encode(group.name))
        }
        when (group.quality) {
            Group.Quality.Best -> url.append("/best")
            Group.Quality.All -> url.append("/all")
        }
        if (pageId != null) url.append("/" + pageId)
        return "" + url
    }
}
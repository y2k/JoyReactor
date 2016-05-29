package y2k.joyreactor.services.requests

import y2k.joyreactor.model.Group

import java.net.URLEncoder

/**
 * Created by y2k on 11/8/15.
 */
class UrlBuilder {

    fun build(group: Group, pageId: String?): String {
        val url = StringBuilder("/")
        when (group.type) {
            Group.Type.User -> url.append("user/" + URLEncoder.encode(group.name))
            Group.Type.Tag -> url.append("tag/" + URLEncoder.encode(group.name))
            Group.Type.Favorite ->
                url.append("user/").append(URLEncoder.encode(group.name)).append("/favorite")
        }
        when (group.quality) {
            Group.Quality.Best -> url.append("/best")
            Group.Quality.All -> url.append("/all")
        }
        if (pageId != null) url.append("/" + pageId)
        return "http://joyreactor.cc" + url.replace(Regex("/+"), "/")
    }
}
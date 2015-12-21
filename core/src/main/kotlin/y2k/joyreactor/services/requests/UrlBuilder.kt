package y2k.joyreactor.services.requests

import y2k.joyreactor.Tag

import java.net.URLEncoder

/**
 * Created by y2k on 11/8/15.
 */
internal class UrlBuilder {

    fun build(tag: Tag, pageId: String?): String {
        var url = "http://joyreactor.cc/"
        if (tag.isFavorite)
            url += "user/" + URLEncoder.encode(tag.username)
        else if (tag.serverId != null)
            url += "tag/" + URLEncoder.encode(tag.serverId)
        if (pageId != null) url += "/" + pageId
        return url
    }
}
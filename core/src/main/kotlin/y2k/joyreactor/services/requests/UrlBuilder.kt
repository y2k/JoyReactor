package y2k.joyreactor.services.requests

import y2k.joyreactor.model.Group

import java.net.URLEncoder

/**
 * Created by y2k on 11/8/15.
 */
internal class UrlBuilder {

    fun build(group: Group, pageId: String?): String {
        var url = "http://joyreactor.cc/"
        if (group.isFavorite)
            url += "user/" + URLEncoder.encode(group.username)
        else if (group.serverId != null)
            url += "tag/" + URLEncoder.encode(group.serverId)
        if (pageId != null) url += "/" + pageId
        return url
    }
}
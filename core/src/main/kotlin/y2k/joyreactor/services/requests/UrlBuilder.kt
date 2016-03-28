package y2k.joyreactor.services.requests

import y2k.joyreactor.model.Group

import java.net.URLEncoder

/**
 * Created by y2k on 11/8/15.
 */
internal class UrlBuilder {

    fun build(group: Group, pageId: String?): String {
        var url = "http://joyreactor.cc/"
        if (group.type == Group.Type.User)
            url += "user/" + URLEncoder.encode(group.name)
        else if (group.type == Group.Type.Tag)
            url += "tag/" + URLEncoder.encode(group.name)
        if (pageId != null) url += "/" + pageId
        return url
    }
}
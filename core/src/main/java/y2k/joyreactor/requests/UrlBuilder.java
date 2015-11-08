package y2k.joyreactor.requests;

import y2k.joyreactor.Tag;

import java.net.URLEncoder;

/**
 * Created by y2k on 11/8/15.
 */
class UrlBuilder {

    public String build(Tag tag, String pageId) {
        String url = "http://joyreactor.cc/";
        if (tag.isFavorite())
            url += "user/" + URLEncoder.encode(tag.getUsername());
        else if (tag.getId() != null)
            url += "tag/" + URLEncoder.encode(tag.getId());
        if (pageId != null) url += "/" + pageId;
        return url;
    }
}
package y2k.joyreactor.services.requests

import y2k.joyreactor.common.PersistentMap
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.platform.Platform
import java.net.URLEncoder

/**
 * Created by y2k on 10/18/15.
 */
class TagImageRequest(
    private val httpClient: HttpClient,
    platform: Platform) {

    private val cache = PersistentMap("tag-images.1.dat", platform)

    fun request(tag: String): String {
        sStorage = IconStorage[sStorage, "tag.names", "tag.icons"]

        val clearTag = tag.toLowerCase()
        var imageId = getImageId(clearTag)

        if (imageId == null) imageId = cache.get(clearTag)
        if (imageId == null) {
            imageId = getFromWeb(clearTag)
            cache.put(clearTag, imageId).flush()
        }
        return imageId
    }

    private fun getImageId(clearTag: String): String? {
        val id = sStorage!!.getImageId(clearTag)
        return if (id == null) null else "http://img0.reactor.cc/pics/avatar/tag/" + id
    }

    private fun getFromWeb(tag: String): String {
        val doc = httpClient.getDocument("http://joyreactor.cc/tag/" + URLEncoder.encode(tag))
        val result = doc.select("img.blog_avatar").first().attr("src")
        println("Not found in cache | $tag | $result")
        return result
    }

    companion object {

        private var sStorage: IconStorage? = null
    }
}
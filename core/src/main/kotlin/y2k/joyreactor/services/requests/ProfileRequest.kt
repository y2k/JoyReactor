package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.getDocumentAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Profile
import java.util.regex.Pattern

/**
 * Created by y2k on 19/10/15.
 */
class ProfileRequest(
    private val httpClient: HttpClient,
    private val requestUsername: () -> CompletableFuture<String>) :
    Function1<String, CompletableFuture<Profile>> {

    override operator fun invoke(username: String): CompletableFuture<Profile> {
        return getUrl(username)
            .let { httpClient.getDocumentAsync(it) }
            .thenAsync { ProfileParser(it).parseAsync() }
    }

    @Deprecated("Use operator invoke + UserNameRequest")
    fun request(): CompletableFuture<Profile> {
        return requestUsername()
            .thenAsync { httpClient.getDocumentAsync(getUrl(it)) }
            .thenAsync { ProfileParser(it).parseAsync() }
    }

    private fun getUrl(username: String) = "http://joyreactor.cc/user/" + username

    private class ProfileParser(
        private val document: Document) {

        fun parseAsync(): CompletableFuture<Profile> {
            return runAsync {
                Profile(
                    document.select("div.sidebarContent > div.user > span").text(),
                    Image(document.select("div.sidebarContent > div.user > img").attr("src"), 0, 0),
                    document.select("#rating-text > b").text().replace(" ", "").toFloat(),
                    document.select(".star-row-0 > .star-0").size,
                    getProgressToNewStar(),
                    getSubRatings(),
                    getAwards())
            }
        }

        private fun getProgressToNewStar(): Float {
            val style = document.select("div.stars div.poll_res_bg_active").first().attr("style")
            val m = Pattern.compile("width:(\\d+)%;").matcher(style)
            if (!m.find()) throw IllegalStateException()
            return java.lang.Float.parseFloat(m.group(1))
        }

        private fun getSubRatings(): List<Profile.SubRating> {
            return document
                .select("div.blogs tr")
                .filter { !it.select("small").isEmpty() }
                .map {
                    Profile.SubRating(
                        "\\d[\\d\\. ]*".toRegex().find(it.select("small").text())!!.value.replace(" ", "").toFloat(),
                        it.select("img").attr("alt"))
                }
        }

        private fun getAwards(): List<Profile.Award> {
            return document
                .select("div.award_holder > img")
                .map { Profile.Award(it.absUrl("src"), it.attr("alt")) }
        }
    }
}
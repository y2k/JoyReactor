package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import rx.Observable
import y2k.joyreactor.Image
import y2k.joyreactor.Profile
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient
import java.util.regex.Pattern

/**
 * Created by y2k on 19/10/15.
 */
class ProfileRequestFactory {

    fun request(): Observable<Profile> {
        return UserNameRequest()
                .request()
                .flatMap({ username ->
                    ObservableUtils.create<Profile> {
                        val page = HttpClient.getInstance().getDocument(getUrl(username))
                        ProfileParser(page).parse()
                    }
                })
    }

    private fun getUrl(username: String?): String {
        if (username == null) throw RuntimeException()
        return "http://joyreactor.cc/user/" + username
    }

    private class ProfileParser(private val document: Document) {

        fun parse(): Profile {
            val profile = Profile()
            profile.userName = document.select("div.sidebarContent > div.user > span").text()
            profile.userImage = Image(document.select("div.sidebarContent > div.user > img").attr("src"), 0, 0)
            profile.progressToNewStar = progressToNewStar
            profile.rating = java.lang.Float.parseFloat(document.select("#rating-text > b").text())
            profile.stars = document.select(".star-row-0 > .star-0").size
            return profile
        }

        private val progressToNewStar: Float
            get() {
                val style = document.select("div.poll_res_bg_active").first().attr("style")
                val m = Pattern.compile("width:(\\d+)%;").matcher(style)
                if (!m.find()) throw IllegalStateException()
                return java.lang.Float.parseFloat(m.group(1))
            }
    }
}
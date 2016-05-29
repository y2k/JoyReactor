package y2k.joyreactor.services.requests.parser

import org.jsoup.nodes.Element
import y2k.joyreactor.model.MyLike

class LikeParser(private val element: Element) {

    val myLike: MyLike
        get() {
            if (element.children().size == 0) return MyLike.Blocked
            if (!element.select("div.vote-minus.vote-change").isEmpty()) return MyLike.Like
            if (!element.select("div.vote-plus.vote-change").isEmpty()) return MyLike.Dislike
            return MyLike.Unknown
        }
}
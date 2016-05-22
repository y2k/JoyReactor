package y2k.joyreactor.requests

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by y2k on 5/3/16.
 */
object MockRequest {

    fun loadDocument(name: String): Document {
        return Jsoup.parse(load(name))
    }

    fun load(name: String): String {
        val resourceAsStream = javaClass.getResourceAsStream("/mock-responses/$name")
            ?: throw Exception("Resource $name not found")

        return resourceAsStream.use { it.bufferedReader().readText() }
    }
}
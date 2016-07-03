package y2k.joyreactor.requests

import org.jsoup.nodes.Document
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.RequestBuilder
import java.io.File

/**
 * Created by y2k on 03/07/16.
 */
class MockHttpClient : HttpClient {

    override fun getDocument(url: String): Document {
        return "/post/(\\d+)".toRegex()
            .find(url)!!.groupValues[1]
            .let { MockRequest.loadDocument("$it.html") }
    }

    override fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getText(url: String): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun clearCookies() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun buildRequest(): RequestBuilder {
        throw UnsupportedOperationException("not implemented")
    }
}
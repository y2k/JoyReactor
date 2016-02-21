package y2k.joyreactor.platform

import org.jsoup.nodes.Document
import y2k.joyreactor.common.NetworkIndicator
import y2k.joyreactor.http.HttpClient

import java.io.File

/**
 * Created by y2k on 10/13/15.
 */
class NetworkActivityIndicatorHttpClient : HttpClient() {

    private val indicator = NetworkIndicator()

    override fun getDocument(url: String): Document {
        try {
            indicator.setEnabled(true)
            return super.getDocument(url)
        } finally {
            indicator.setEnabled(false)
        }
    }

    override fun getText(url: String): String {
        try {
            indicator.setEnabled(true)
            return super.getText(url)
        } finally {
            indicator.setEnabled(false)
        }
    }

    override fun downloadToFile(url: String, file: File, callback: Function2<Int, Int, Unit>?) {
        try {
            indicator.setEnabled(true)
            super.downloadToFile(url, file, callback)
        } finally {
            indicator.setEnabled(false)
        }
    }
}
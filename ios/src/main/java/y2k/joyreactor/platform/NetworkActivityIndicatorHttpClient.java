package y2k.joyreactor.platform;

import org.jsoup.nodes.Document;
import rx.functions.Action2;
import y2k.joyreactor.NetworkIndicator;
import y2k.joyreactor.http.HttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by y2k on 10/13/15.
 */
public class NetworkActivityIndicatorHttpClient extends HttpClient {

    private NetworkIndicator indicator = new NetworkIndicator();

    @Override
    public Document getDocument(String url) throws IOException {
        try {
            indicator.setEnabled(true);
            return super.getDocument(url);
        } finally {
            indicator.setEnabled(false);
        }
    }

    @Override
    public String getText(String url) throws IOException {
        try {
            indicator.setEnabled(true);
            return super.getText(url);
        } finally {
            indicator.setEnabled(false);
        }
    }

    @Override
    public void downloadToFile(String url, File file, Action2<Integer, Integer> callback) throws IOException {
        try {
            indicator.setEnabled(true);
            super.downloadToFile(url, file, callback);
        } finally {
            indicator.setEnabled(false);
        }
    }
}
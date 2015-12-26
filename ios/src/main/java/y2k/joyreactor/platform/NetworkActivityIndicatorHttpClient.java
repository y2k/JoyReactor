package y2k.joyreactor.platform;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import y2k.joyreactor.NetworkIndicator;
import y2k.joyreactor.http.HttpClient;

import java.io.File;

/**
 * Created by y2k on 10/13/15.
 */
public class NetworkActivityIndicatorHttpClient extends HttpClient {

    private NetworkIndicator indicator = new NetworkIndicator();

    @Override
    public Document getDocument(String url) {
        try {
            indicator.setEnabled(true);
            return super.getDocument(url);
        } finally {
            indicator.setEnabled(false);
        }
    }

    @Override
    public String getText(String url) {
        try {
            indicator.setEnabled(true);
            return super.getText(url);
        } finally {
            indicator.setEnabled(false);
        }
    }

    @Override
    public void downloadToFile(@NotNull String url, @NotNull File file, @Nullable Function2<? super Integer, ? super Integer, ? extends Unit> callback) {
        try {
            indicator.setEnabled(true);
            super.downloadToFile(url, file, callback);
        } finally {
            indicator.setEnabled(false);
        }
    }
}
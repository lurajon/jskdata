package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Stupid, but just for completeness. And by the way, in the perfect world, all
 * data sets should be downloadable from a static public URL.
 */
class URLDownloader extends Downloader {

    private final Set<String> urls = new HashSet<>();

    public URLDownloader() {
    }

    @Override
    public void login() throws IOException {
    }

    @Override
    public void dataset(String url) throws IOException {
        if (!url.startsWith("http")) {
            throw new IllegalArgumentException("invalid url");
        }
        if (url.indexOf('/') <= 0) {
            throw new IllegalArgumentException("invalid url");
        }
        urls.add(url);
    }

    @Override
    public void download(Receiver receiver) throws IOException {
        for (String url : urls) {
            if (receiver.shouldStop()) {
                return;
            }

            String fileName = url.substring(url.lastIndexOf('/') + 1);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            receiver.receive(fileName, conn.getInputStream());
        }
    }

    @Override
    public void clear() {
    }

}

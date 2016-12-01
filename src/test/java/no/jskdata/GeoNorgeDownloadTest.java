package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;

public class GeoNorgeDownloadTest extends DownloaderTestCase {

    private static final String USERNAME_KEY = "geonorge.username";

    private static final String PASSWORD_KEY = "geonorge.password";

    public void testDownload() throws IOException {

        String username = getProperty(USERNAME_KEY);
        String password = getProperty(PASSWORD_KEY);

        Downloader gnd = new GeoNorgeDownload(username, password);
        gnd.login();
        gnd.setFileNameFilter(fileName -> fileName.endsWith("_Ledning.zip"));
        gnd.dataset("FKB-data");
        Set<String> fileNames = new HashSet<>();
        for (HttpURLConnection conn : gnd.downloads()) {
            assertEquals(200, conn.getResponseCode());
            assertEquals("application/octet-stream", conn.getHeaderField("Content-Type"));
            for (String fileName : fileNamesFromZip(conn.getInputStream())) {
                assertFalse(fileNames.contains(fileName));
                fileNames.add(fileName);
            }
            if (fileNames.size() > 3) {
                break;
            }
        }
        assertFalse(fileNames.isEmpty());

        // clear download list and selection
        gnd.clear();

    }

}

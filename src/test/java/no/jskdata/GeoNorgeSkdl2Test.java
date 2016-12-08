package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.ByteStreams;

public class GeoNorgeSkdl2Test extends DownloaderTestCase {

    private static final String USERNAME_KEY = "geonorge.username";

    private static final String PASSWORD_KEY = "geonorge.password";

    public void testDownload() throws IOException {

        String username = getProperty(USERNAME_KEY);
        String password = getProperty(PASSWORD_KEY);

        Downloader gnd = new GeoNorgeSkdl2(username, password);
        gnd.login();
        gnd.setFileNameFilter(f -> f.endsWith("_Ledning.zip"));
        gnd.dataset("FKB-data");
        Set<String> fileNames = new HashSet<>();
        gnd.download(new Receiver() {

            @Override
            public boolean shouldStop() {
                return fileNames.size() > 10;
            }

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                assertFalse(fileNames.contains(fileName));
                assertTrue(fileName.endsWith("_Ledning.zip"));
                fileNames.add(fileName);
                ByteStreams.exhaust(in);
            }

        });
        assertFalse(fileNames.isEmpty());

        // clear download list and selection
        gnd.clear();

    }

}

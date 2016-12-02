package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.ByteStreams;

public class KartverketDownloadTest extends DownloaderTestCase {

    private static final String USERNAME_KEY = "data.kartverket.no.username";

    private static final String PASSWORD_KEY = "data.kartverket.no.password";

    public void testDownload() throws IOException {

        String username = getProperty(USERNAME_KEY);
        String password = getProperty(PASSWORD_KEY);

        Downloader kd = new KartverketDownload(username, password);
        kd.login();
        Set<String> fileNames = new HashSet<>();

        // test single file
        fileNames.clear();
        kd.dataset("n5000-kartdata-utm-33-hele-landet");
        kd.download(new DefaultReceiver() {

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                assertFalse(fileNames.contains(fileName));
                fileNames.add(fileName);
                ByteStreams.exhaust(in);
            }
        });
        assertEquals(1, fileNames.size());
        kd.clear();

        // test multiple files
        kd.dataset("administrative-fylker-utm-32-fylkesinndeling");
        kd.download(new Receiver() {

            @Override
            public boolean shouldStop() {
                return fileNames.size() > 3;
            }

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                assertFalse(fileNames.contains(fileName));
                fileNames.add(fileName);
                ByteStreams.exhaust(in);
            }

        });
        assertFalse(fileNames.isEmpty());
        kd.clear();

        // test with BiConsumer
        fileNames.clear();
        kd.setFileNameFilter(f -> f.contains("Nordland"));
        kd.dataset("stedsnavn-ssr-sosi-utm33");
        kd.download((fileName, in) -> {
            assertTrue(fileName.contains("Nordland"));
            fileNames.add(fileName);
            try {
                ByteStreams.exhaust(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(1, fileNames.size());

    }

}

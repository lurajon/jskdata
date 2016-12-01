package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

        for (String datasetId : Arrays.asList("administrative-fylker-utm-32-fylkesinndeling",
                "vbase-utm-33-fylkesinndeling")) {
            kd.dataset(datasetId);

            Set<String> fileNames = new HashSet<>();
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
        }

        kd.clear();

        Set<String> fileNames = new HashSet<>();
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

    public void testCreateUrl() {
        assertEquals(
                "http://data.kartverket.no/download/system/files/kartdata/n50/kommuner/Kartdata_441_Os_UTM33_N50_SOSI.zip",
                KartverketDownload.createUrl("n50-kartdata-utm-33-kommunevis-inndeling",
                        "Kartdata_441_Os_UTM33_N50_SOSI.zip"));
        assertEquals(
                "http://data.kartverket.no/download/system/files/grensedata/fylker/Grensedata_5_Oppland_UTM32_Adm_enheter_SOSI.zip",
                KartverketDownload.createUrl("administrative-fylker-utm-33-fylkesinndeling",
                        "Grensedata_5_Oppland_UTM32_Adm_enheter_SOSI.zip"));
    }

}

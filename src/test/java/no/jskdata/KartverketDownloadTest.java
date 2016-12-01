package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KartverketDownloadTest extends DownloaderTestCase {

    private static final String USERNAME_KEY = "data.kartverket.no.username";

    private static final String PASSWORD_KEY = "data.kartverket.no.password";

    public void testDownload() throws IOException {

        String username = getProperty(USERNAME_KEY);
        String password = getProperty(PASSWORD_KEY);

        Downloader kd = new KartverketDownload(username, password);
        kd.login();

        Set<String> fileNames = new HashSet<>();
        for (String datasetId : Arrays.asList("administrative-fylker-utm-32-fylkesinndeling",
                "vbase-utm-33-fylkesinndeling")) {
            kd.dataset(datasetId);

            System.out.println("XX " + datasetId);
            for (HttpURLConnection conn : kd.downloads()) {
                assertEquals(200, conn.getResponseCode());
                assertEquals("application/zip", conn.getContentType());
                for (String fileName : fileNamesFromZip(conn.getInputStream())) {
                    assertFalse(fileNames.contains(fileName));
                    fileNames.add(fileName);
                }
            }
            assertFalse(fileNames.isEmpty());
            kd.clear();
        }

        kd.dataset("n50-kartdata-utm-33-kommunevis-inndeling");

        // try to download one of the files
        int connections = 0;
        for (HttpURLConnection conn : kd.downloads()) {
            assertEquals(200, conn.getResponseCode());
            connections++;
            if (connections > 3) {
                break;
            }
        }
        assertTrue(connections > 0);
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

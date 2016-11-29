package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;

import junit.framework.TestCase;

public class KartverketDownloadTest extends TestCase {

    private static final String USERNAME_KEY = "data.kartverket.no.username";

    private static final String PASSWORD_KEY = "data.kartverket.no.password";

    public void testDownload() throws IOException {
        KartverketDownload kdInvalid = new KartverketDownload("nobody", "nobody");
        assertFalse(kdInvalid.hasCookies());
        kdInvalid.login();
        assertFalse(kdInvalid.hasCookies());

        String username = System.getProperty(USERNAME_KEY);
        String password = System.getProperty(PASSWORD_KEY);

        if (username == null) {
            fail("missing " + USERNAME_KEY + " property");
        }
        if (password == null) {
            fail("missing " + PASSWORD_KEY + " property");
        }

        KartverketDownload kd = new KartverketDownload(username, password);
        assertFalse(kd.hasCookies());
        kd.login();
        assertTrue(kd.hasCookies());

        for (String datasetId : Arrays.asList("administrative-fylker-utm-32-fylkesinndeling",
                "vbase-utm-33-fylkesinndeling")) {
            kd.dataset(datasetId);
            assertFalse(kd.urls().isEmpty());
            kd.clear();
            assertTrue(kd.urls().isEmpty());
        }

        kd.dataset("n50-kartdata-utm-33-kommunevis-inndeling");
        assertFalse(kd.urls().isEmpty());

        // try to download one of the files
        String url = kd.urls().iterator().next();
        HttpURLConnection conn = kd.openConnection(url);
        assertEquals(200, conn.getResponseCode());
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

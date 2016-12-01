package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

public class GeoNorgeDownloadTest extends TestCase {

    private static final String USERNAME_KEY = "geonorge.username";

    private static final String PASSWORD_KEY = "geonorge.password";

    public void testDownload() throws IOException {

        String username = System.getProperty(USERNAME_KEY);
        String password = System.getProperty(PASSWORD_KEY);

        if (username == null) {
            fail("missing " + USERNAME_KEY + " property");
        }
        if (password == null) {
            fail("missing " + PASSWORD_KEY + " property");
        }

        GeoNorgeDownload gnd = new GeoNorgeDownload(username, password);
        gnd.login();

        // select some known values
        gnd.select("datasett", "FKB-data");
        gnd.select("kommune", "Asker (0220)");
        gnd.select("format", "SOSI");

        // set file name filter
        gnd.setFileNameFilter(fileName -> fileName.endsWith("_Ledning.zip"));

        // search and download
        HttpURLConnection conn = gnd.download();
        assertEquals(200, conn.getResponseCode());
        assertEquals("application/octet-stream", conn.getHeaderField("Content-Type"));

        // extract files and check file names
        Set<String> fileNames = new HashSet<>();
        ZipInputStream zis = new ZipInputStream(conn.getInputStream());
        ZipEntry e = null;
        while ((e = zis.getNextEntry()) != null) {
            fileNames.add(e.getName());
            assertTrue(e.getName(), e.getName().endsWith("_Ledning.zip"));
        }
        assertFalse(fileNames.isEmpty());

        // clear download list and selection
        gnd.clear();

    }

}

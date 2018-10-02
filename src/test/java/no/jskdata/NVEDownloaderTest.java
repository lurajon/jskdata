package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteStreams;

public class NVEDownloaderTest extends DownloaderTestCase {

    public void testNVEDownload() throws IOException {

        String mailAddress = getProperty("mailAddress");
        String mailServer = getProperty("mailServer");

        Downloader downloader = new NVEDownloader(mailAddress, mailServer);
        downloader.dataset("Flomsoner 50");

        Set<String> fileNames = new HashSet<>();
        Map<String, Long> lengthByFileName = new HashMap<>();
        downloader.download(new DefaultReceiver() {

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                fileNames.add(fileName);
                long l = ByteStreams.exhaust(in);
                lengthByFileName.put(fileName, Long.valueOf(l));
            }
        });
        assertEquals(1, fileNames.size());
        Long length = lengthByFileName.get(fileNames.iterator().next());
        assertNotNull(length);

    }

    public void testFindDownloadURL() throws IOException {
        StringBuilder s = new StringBuilder();
        s.append("Din bestilling av kartdata er fullført\n");
        s.append("\n");
        s.append(
                "   <a href=\"http://195.204.216.68:8080/fmedatadownload/results/NVE_41551B141538486336923_3432.zip\">http://195.204.216.68:8080/fmedatadownload/resultsNVE_41551B14_1538486336923_3432.zip</a> bla bla");
        s.append('\n');
        s.append("   <a href=3D\"http://www.7-zip.org/download.html\">http://www.7-zip.org/download.html</a>   ");

        Set<String> urls = new LinkedHashSet<>();
        NVEDownloader.findDownloadUrls(s.toString(), urls);
        assertEquals(1, urls.size());

        assertEquals("http://195.204.216.68:8080/fmedatadownload/results/NVE_41551B141538486336923_3432.zip",
                urls.iterator().next());

        // cleanup
        s.setLength(0);
        urls.clear();

        s.append("\n");
        s.append("<a href=3D\"http://195.204.216.68:8081/fmedatadownload/results/NVE_41551B14_=\n");
        s.append("1538486336923_3432.zip\">http://195.204.216.68:8081/fmedatadownload/results/=\n");
        s.append("NVE_41551B14_1538486336923_3432.zip</a></br></p>");

        NVEDownloader.findDownloadUrls(s.toString(), urls);
        assertEquals(1, urls.size());

        assertEquals("http://195.204.216.68:8081/fmedatadownload/results/NVE_41551B141538486336923_3432.zip",
                urls.iterator().next());
    }

}

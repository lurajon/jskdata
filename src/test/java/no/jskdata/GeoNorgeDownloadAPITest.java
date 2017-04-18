package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteStreams;

import junit.framework.TestCase;

public class GeoNorgeDownloadAPITest extends TestCase {

    public void testNRL() throws IOException {
        Downloader downloader = new GeoNorgeDownloadAPI();
        downloader.setFileNameFilter(n -> n.contains("SOSI"));
        downloader.dataset("28c896d0-8a0d-4209-bf31-4931033b1082");
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
        assertTrue(length.longValue() > 0l);
        assertTrue(length.longValue() > (1024l * 1024l));
    }

}

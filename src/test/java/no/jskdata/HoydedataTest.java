package no.jskdata;

import com.google.common.io.ByteStreams;
import no.jskdata.data.geonorge.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HoydedataTest extends DownloaderTestCase {

    public void testGetFilelistForDataset() throws IOException{
        Hoydedata h = new Hoydedata();
        List<File> files = h.getFilesforDataset("DTM50","33");
        //expecting 1 file
        assertNotNull(files);
        assertFalse(files.isEmpty());
    }

    public void testGetFilelistForLargeDataset() throws IOException {
        Hoydedata h = new Hoydedata();
        List<File> files = h.getFilesforDataset("DTM1","33");
        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertTrue(files.size()>10);
    }

    public void testDownloadOneDataset() throws IOException {
        Set<String> fileNames = new HashSet<>();

        Hoydedata h = new Hoydedata();
        h.dataset("DTM50");
        h.download(new DefaultReceiver() {

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                assertFalse(fileNames.contains(fileName));
                fileNames.add(fileName);
                ByteStreams.exhaust(in);
            }
        });
        assertEquals(1, fileNames.size());
        h.clear();
    }

    public void testDownloadTwoDatasets() throws IOException {
        Set<String> fileNames = new HashSet<>();
        Hoydedata h = new Hoydedata();
        h.dataset("DTM50");
        h.dataset("DOM50");
        h.download(new DefaultReceiver() {

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                assertFalse(fileNames.contains(fileName));
                fileNames.add(fileName);
                ByteStreams.exhaust(in);
            }
        });
        assertEquals(2, fileNames.size());
        h.clear();
    }

}

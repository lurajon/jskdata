package no.jskdata;

import com.google.common.io.ByteStreams;
import jdk.nashorn.internal.ir.annotations.Ignore;
import no.jskdata.data.geonorge.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Grotan_Bjorn_Ove on 19.12.2017.
 */
public class HoydedataTest extends DownloaderTestCase {

    public void testGetFilelistForDataset() throws IOException{
        Hoydedata h = new Hoydedata();
        List<File> files = h.getFilesforDataset("DTM50","33");
        //expecting 1 file
        assertNotNull(files);
        assertFalse(files.isEmpty());
    }



    public void testDownloadOneDataset() throws IOException {
        Set<String> fileNames = new HashSet<>();
        // test single file
        fileNames.clear();

        Hoydedata h = new Hoydedata();
        h.setUtmzone("33");
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
        h.setUtmzone("33");
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

package no.jskdata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.gson.Gson;

import junit.framework.TestCase;
import no.jskdata.geojson.Feature;
import no.jskdata.geojson.FeatureCollection;

public class AvinorAIPDownloaderTest extends TestCase {

    public void testDownload() throws IOException {
        AvinorAIPDownloader d = new AvinorAIPDownloader();
        d.setMaxCount(2);
        List<Feature> features = new ArrayList<>();
        d.download((fileName, in) -> {
            Reader reader = new InputStreamReader(in);
            FeatureCollection fc = new Gson().fromJson(reader, FeatureCollection.class);
            features.addAll(fc.getFeatures());
        });
        assertEquals(2, features.size());
        for (Feature feature : features) {
            List<?> attachments = (List<?>) feature.getProperty("attachments");
            assertNotNull(attachments);
            assertFalse(attachments.isEmpty());
        }
    }

    public void testMatcher() {
        Matcher matcher = AvinorAIPDownloader.matcher("whatever 583106N 0084227E, REF");
        assertTrue(matcher.find());
        assertEquals("583106N", matcher.group(1));
        assertEquals("0084227E", matcher.group(2));

        matcher = AvinorAIPDownloader.matcher("whatever 583106.33N 0084227.89E, REF");
        assertTrue(matcher.find());
        assertEquals("583106.33N", matcher.group(1));
        assertEquals("0084227.89E", matcher.group(2));
    }

    public void testParseCoordinatePart() {
        // 623345N 0060711E
        assertEquals(62.0 + (33.0 / 60.0) + (45.0 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("623345N", 2), 0.00003);
        assertEquals(6.0 + (7.0 / 60.0) + (11.0 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("0060711E", 3), 0.00003);

        // 623345.00N 0060711.00E
        assertEquals(62.0 + (33.0 / 60.0) + (45.0 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("623345.00N", 2), 0.00003);
        assertEquals(6.0 + (7.0 / 60.0) + (11.0 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("0060711.00E", 3), 0.00003);

        // 623345.50N 0060711.500E
        assertEquals(62.0 + (33.0 / 60.0) + (45.5 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("623345.50N", 2), 0.00003);
        assertEquals(6.0 + (7.0 / 60.0) + (11.5 / (60.0 * 60.0)),
                AvinorAIPDownloader.parseCoordinatePart("0060711.50E", 3), 0.00003);
    }

}

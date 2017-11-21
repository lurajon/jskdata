package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

public abstract class DownloaderTestCase extends TestCase {
    
    protected static final String USERNAME_KEY = "geonorge.username";

    protected static final String PASSWORD_KEY = "geonorge.password";
    
    protected String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }


    protected String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            fail("missing " + key + " property");
        }
        return value;
    }

    protected Set<String> fileNamesFromZip(InputStream in) throws IOException {
        Set<String> fileNames = new HashSet<>();
        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry e = null;
        while ((e = zis.getNextEntry()) != null) {
            assertFalse(fileNames.contains(e.getName()));
            fileNames.add(e.getName());
        }
        return Collections.unmodifiableSet(fileNames);
    }

}

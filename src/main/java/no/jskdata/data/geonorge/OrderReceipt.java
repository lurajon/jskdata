package no.jskdata.data.geonorge;

import java.util.Collections;
import java.util.List;

public class OrderReceipt {

    public String referenceNumber;
    public List<File> files;

    public List<File> getFiles() {
        if (files == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(files);
    }

}

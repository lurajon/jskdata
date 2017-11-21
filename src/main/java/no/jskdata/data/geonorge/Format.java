package no.jskdata.data.geonorge;

import java.util.ArrayList;
import java.util.List;

public class Format {

    public String name;
    public String version;

    public List<String> _links = new ArrayList<>();
    
    @Override
    public String toString() {
        return name;
    }

}

package no.jskdata.data.geonorge;

import java.util.ArrayList;
import java.util.List;

public class Projection implements Comparable<Projection> {
    
    public String code;
    public String name;
    public String codespace;
    
    public List<String> _links = new ArrayList<>();

    @Override
    public int compareTo(Projection o) {
        // prefer UTM as NRL are currently not available in all the projections
        // it announces
        if (o.code.equals(code)) {
            return 0;
        }
        if (name.contains("UTM") && o.name.equals("UTM")) {
            return 0;
        }
        if (name.contains("UTM")) {
            return -1;
        }
        if (o.name.contains("UTM")) {
            return 1;
        }
        return 0;
    }

}

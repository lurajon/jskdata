package no.jskdata.geojson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeatureCollection {

    @SuppressWarnings("unused")
    private String type = "FeatureCollection";

    private List<Feature> features = new ArrayList<>();

    public void add(Feature feature) {
        features.add(feature);
    }
    
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    public int size() {
        return features.size();
    }

}

package no.jskdata.geojson;

import java.util.HashMap;
import java.util.Map;

public class Feature {

    @SuppressWarnings("unused")
    private String type = "Feature";

    private Map<String, Object> properties = new HashMap<>();
    
    @SuppressWarnings("unused")
    private Geometry geometry;
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    
}

package no.jskdata.data.geonorge;

import java.util.List;

public class Capabilities {

    public boolean supportsProjectionSelection;
    public boolean supportsFormatSelection;
    public boolean supportsPolygonSelection;
    public boolean supportsAreaSelection;

    public List<CapabilitiesLink> _links;

    private String hrefForRel(String rel) {
        if (_links == null || rel == null) {
            return null;
        }

        for (CapabilitiesLink link : _links) {
            if (rel.equals(link.rel)) {
                return link.href;
            }
        }

        return null;
    }

    public String getProjectionUrl() {
        return hrefForRel("http://rel.geonorge.no/download/projection");
    }

    public String getFormatUrl() {
        return hrefForRel("http://rel.geonorge.no/download/format");
    }

    public String getAreaUrl() {
        return hrefForRel("http://rel.geonorge.no/download/area");
    }

    public String getOrderUrl() {
        return hrefForRel("http://rel.geonorge.no/download/order");
    }

    public String getCanDownloadUrl() {
        return hrefForRel("http://rel.geonorge.no/download/can-download");
    }
}

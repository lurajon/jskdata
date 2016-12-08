package no.jskdata.data.geonorge;

import java.util.List;

public class Area {

    public String code;
    public String type;
    public String name;

    public List<Projection> projections;
    public List<Format> formats;

    public boolean isCountryWide() {
        return "0000".equals(code);
    }

    public boolean isCounty() {
        return code != null && code.length() == 2;
    }

    public boolean isMunicipal() {
        return code != null && code.length() == 4;
    }

    public OrderArea asOrderArea() {
        OrderArea oa = new OrderArea();
        oa.code = code;
        oa.type = type;
        oa.name = name;
        return oa;
    }

}

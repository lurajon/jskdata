package no.jskdata.data.geonorge;

import java.util.ArrayList;
import java.util.List;

/**
 * @see https://nedlasting.geonorge.no/Help/ResourceModel?modelName=Geonorge.
 *      NedlastingApi.V1.OrderLineType
 */
public class OrderLine {

    public List<OrderArea> areas = new ArrayList<>();
    public List<Format> formats = new ArrayList<>();
    public String metadataUuid;
    public String coordinates;
    public List<Projection> projections = new ArrayList<>();

}

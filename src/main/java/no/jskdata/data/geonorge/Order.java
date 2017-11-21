package no.jskdata.data.geonorge;

import java.util.ArrayList;
import java.util.List;

/**
 * @see https://nedlasting.geonorge.no/Help/ResourceModel?modelName=Geonorge.
 *      NedlastingApi.V1.OrderType
 */
public class Order {

    public String email = "";
    public final List<OrderLine> orderLines = new ArrayList<>();

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
    }

    public boolean isEmpty() {
        return orderLines.isEmpty();
    }

}

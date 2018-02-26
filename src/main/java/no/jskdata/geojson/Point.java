package no.jskdata.geojson;

public class Point extends Geometry {

    public Point(double x, double y) {
        type = "Point";
        coordinates = new double[] { x, y };
    }

}

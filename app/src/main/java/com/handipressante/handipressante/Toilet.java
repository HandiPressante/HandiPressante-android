package com.handipressante.handipressante;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements IMarker {
    public String getMarkerName() {
        return "NoName";
    }

    public GPSCoordinates getCoordinates() {
        return new GPSCoordinates();
    }
}

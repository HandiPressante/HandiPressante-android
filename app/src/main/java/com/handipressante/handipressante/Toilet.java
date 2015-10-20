package com.handipressante.handipressante;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements IMarker {
    private Integer _id;
    private Boolean _adapted;
    private String _address;
    private GPSCoordinates _coord;

    Toilet(Integer id, Boolean adapted, String address, GPSCoordinates coord) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coord = coord;
    }

    public Integer getId() {
        return _id;
    }

    public Boolean isAdapted() {
        return _adapted;
    }

    public String getAddress() {
        return _address;
    }

    public Integer getRank() {
        return 4;
    }

    public  Double getDistance(GPSCoordinates ref) {
        // TODO : compute dist between _coord and ref
        return new Double(42);
    }

    public String getMarkerName() {
        return "NoName";
    }

    public GPSCoordinates getCoordinates() {
        return new GPSCoordinates();
    }
}

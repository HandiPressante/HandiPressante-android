package com.handipressante.handipressante;

/**
 * Created by Nico on 19/10/2015.
 */
public class GPSCoordinates {
    private double _latitude;
    private double _longitude;

    public GPSCoordinates() {
        _latitude = 0;
        _longitude = 0;
    }

    public GPSCoordinates(double latitude, double longitude) {
        _latitude = latitude;
        _longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this._latitude = latitude;
    }

    public double getLatitude() {
        return this._latitude;
    }

    public double getL93X() { return 351861.03; }

    public double getL93Y() { return 6789173.05; }

    public void setLongitude(double longitude) {
        this._longitude = longitude;
    }

    public double getLongitude() {
        return this._longitude;
    }

}

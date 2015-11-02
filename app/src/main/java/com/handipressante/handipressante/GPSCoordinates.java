package com.handipressante.handipressante;

/**
 * Created by Nico on 19/10/2015.
 */
public class GPSCoordinates {
    private double _x93;
    private double _y93;

    public GPSCoordinates() {
        _x93 = 0;
        _y93 = 0;
    }

    public GPSCoordinates(double x93, double y93) {
        _x93 = x93;
        _y93 = y93;
    }

    public void setL93X(double x93) {
        this._x93 = x93;
    }
    public void setL93Y(double y93) {
        this._y93 = y93;
    }

    public double getL93X() { return _x93; }

    public double getL93Y() { return _y93; }
}

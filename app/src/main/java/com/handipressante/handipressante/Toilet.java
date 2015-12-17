package com.handipressante.handipressante;

import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;

/**
 * Created by Nico on 19/10/2015.
 */
public class Toilet implements IMarker {
    private Integer _id;
    private Boolean _adapted;
    private String _address;
    private GPSCoordinates _coord;
    private GeoPoint _coordGeo;
    private Integer _rank; // from 1 to 5

    Toilet(Integer id, Boolean adapted, String address, GPSCoordinates coord, Integer rank) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coord = coord;
        _rank = rank;
    }

    Toilet(Integer id, Boolean adapted, String address, GeoPoint coord, Integer rank) {
        _id = id;
        _adapted = adapted;
        _address = address;
        _coordGeo = coord;
        _rank = rank;
    }

    public GeoPoint getGeo() {
        return _coordGeo;
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
        return _rank;
    }

    public int getIcon() {
        if (_adapted) {
            return R.drawable.handicap_icon;
        } else {
            return R.drawable.not_handicap_icon;
        }
    }

    public int getRankIcon() {

        switch (_rank) {
            case 1:
                return R.drawable.star_one;
            case 2:
                return R.drawable.star_two;
            case 3:
                return R.drawable.star_three;
            case 4:
                return R.drawable.star_four;
            case 5:
                return R.drawable.star_five;
            default:
                return R.drawable.star_zero;

        }

    }

    // return distance in meters
    public Double getDistance(GPSCoordinates ref) {
        double x1 = _coord.getL93X();
        Log.d("Debug", "x1 = " + x1);

        double x2 = ref.getL93X();
        Log.d("Debug", "x2 = " + x2);

        double y1 = _coord.getL93Y();
        double y2 = ref.getL93Y();
        Log.d("Debug", "x1 - x2 = " + (x1 - x2));
        Log.d("Debug", "(x1 - x2)² = " + Math.pow(x1 - x2, 2));

        return Math.sqrt(0 + Math.pow(_coord.getL93Y() - ref.getL93Y(), 2));

        /*
        // TODO : compute dist between _coord and ref
        switch (Gitan) {
            case 0:
                return (double) 167.23;
            case 1:
                return (double) 367.56;
            case 2:
                return (double) 641;
            case 3:
                return (double) 871;
            case 4:
                return (double) 1203.23;
            case 5:
                return (double) 1607.23;
            case 6:
                return (double) 2168.23;
            case 7:
                return (double) 2767.23;
            case 8:
                return (double) 3132.23;
            case 9:
                return (double) 3385.23;
            default:
                return (double) 4345.5;
        }*/
    }

    // return a string to display the distance
    public String getDistanceToString(GPSCoordinates ref) {
        Double dist = this.getDistance(ref);
        if (dist > 1000) {
            // Kilometers
            dist = dist / 1000;
            return (new DecimalFormat(".#").format(dist)) + " Km";
        } else {
            // meters
            return dist.intValue() + " m";
        }
    }

    public String getMarkerName() {
        return "NoName";
    }

    public GPSCoordinates getCoordinates() {
        return _coord;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Toilet) {
            Toilet t = (Toilet) o;
            return this._id.equals(t._id);
        }

        return false;
    }
}

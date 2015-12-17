package com.handipressante.handipressante;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class TestDataModel implements IDataModel {
    ArrayList<Toilet> toiletsList;

    public TestDataModel() {

        GeoPoint loc = new GeoPoint(48.1157242, -1.6443362);
        GeoPoint loc_bis = new GeoPoint(loc);
        loc_bis.setLatitudeE6(loc_bis.getLatitudeE6() + 10000);
        GeoPoint loc_ter = new GeoPoint(loc);
        loc_ter.setLongitudeE6(loc_bis.getLongitudeE6() + 10000);

        Toilet t1 = new Toilet(80, false, "Départ info, étage, couloir TP", loc, 0);
        Toilet t2 = new Toilet(2, true, "Amphi D", loc_bis, 1);
        Toilet t3 = new Toilet(67, true, "Département STPI RdC", loc_ter, 2);

        toiletsList = new ArrayList<Toilet>();
        toiletsList.add(t1);
        toiletsList.add(t2);
        toiletsList.add(t3);

    }

    public Toilet getToilet(GeoPoint geo) {
        for (Toilet t : toiletsList) {
            if (t.getGeo().equals(geo)) {
                return t;
            }
        }
        return null;
    }

    public Toilet getToilet(int id){
        for (Toilet t : toiletsList) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public List<Toilet> getToilets(double long_min, double lat_max, double long_max, double lat_min) {

        return toiletsList;
    }

    public List<Toilet> getToilets(GPSCoordinates ref, double xRange, double yRange) {
        ArrayList<Toilet> res = new ArrayList<>();

        res.add(new Toilet(1, false, "Départ info, étage, couloir TP", new GPSCoordinates(), 0));
        res.add(new Toilet(2, true, "Amphi D", new GPSCoordinates(), 1));
        res.add(new Toilet(3, true, "Département STPI RdC", new GPSCoordinates(), 2));
        res.add(new Toilet(4, true, "Département STPI 1er étage", new GPSCoordinates(), 3));

        return res;
    }
}

package com.handipressante.handipressante;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class TestDataModel implements IDataModel {
    ArrayList<Toilet> _toilets;

    public TestDataModel() {
        _toilets = new ArrayList<>();

        GeoPoint loc = new GeoPoint(48.1157242, -1.6443362);
        GeoPoint loc_bis = new GeoPoint(loc);
        loc_bis.setLatitudeE6(loc_bis.getLatitudeE6() + 10000);
        GeoPoint loc_ter = new GeoPoint(loc);
        loc_ter.setLongitudeE6(loc_bis.getLongitudeE6() + 10000);

        Toilet t1 = new Toilet(80, false, "Départ info, étage, couloir TP", loc, 50.0);
        Toilet t2 = new Toilet(2, true, "Amphi D", loc_bis, 60.0);
        t2.setRankAverage(1);
        Toilet t3 = new Toilet(67, true, "Département STPI RdC", loc_ter, 20.0);
        t3.setRankAverage(2);

        _toilets.add(t1);
        _toilets.add(t2);
        _toilets.add(t3);

    }

    public Toilet getToilet(GeoPoint geo) {
        for (Toilet t : _toilets) {
            if (t.getGeo().equals(geo)) {
                return t;
            }
        }
        return null;
    }

    public Toilet getToilet(int id){
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public List<Toilet> getToiletsList(GeoPoint ref, int mincount, int maxcount, int distanceMax) {
        return _toilets;
    }

    public List<Toilet> getToiletsMap(GeoPoint topLeft, GeoPoint bottomRight) {
        return _toilets;
    }

    public Toilet getToiletFromCache(int id) {
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) return t;
        }

        return new Toilet();
    }
}

package com.handipressante.handipressante;

import android.content.Context;
import android.util.Log;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


public class TestDataModel implements IDataModel {
    private List<PropertyChangeListener> _listeners = new ArrayList<>();
    private List<Toilet> _toilets = new ArrayList<>();

    public TestDataModel() {
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

    @Override
    public boolean containsToilet(int id) {
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) return true;
        }

        return false;
    }

    // TODO : Remove
    public Toilet getToilet(GeoPoint geo) {
        for (Toilet t : _toilets) {
            if (t.getGeo().equals(geo)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax) {
        System.out.println("Nico requestNearbyToilets");
        notifyListeners(this, "NearbyToilets", null, _toilets);
    }

    @Override
    public void requestMapToilets(GeoPoint topLeft, GeoPoint bottomRight) {
        notifyListeners(this, "MapToilets", null, null);
    }

    @Override
    public void requestToilet(int id) {
        notifyListeners(this, "Toilets", null, null);
    }

    @Override
    public List<Toilet> getNearbyToilets() {
        return _toilets;
    }

    @Override
    public List<Toilet> getMapToilets() {
        return _toilets;
    }

    @Override
    public Toilet getToilet(int id){
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
        _listeners.add(listener);
    }

    private void notifyListeners(Object o, String property, Object oldValue, Object newValue) {
        for (PropertyChangeListener listener : _listeners) {
            listener.propertyChange(new PropertyChangeEvent(o, property, oldValue, newValue));
        }
    }
}

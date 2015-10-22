package com.handipressante.handipressante;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class TestDataModel implements IDataModel {
    public List<Toilet> getToilets(GPSCoordinates ref, double xRange, double yRange) {
        ArrayList<Toilet> res = new ArrayList<>();

        res.add(new Toilet(1, false, "Départ info, étage, couloir TP", new GPSCoordinates(), 0));
        res.add(new Toilet(2, true, "Amphi D", new GPSCoordinates(), 1));
        res.add(new Toilet(3, true, "Département STPI RdC", new GPSCoordinates(), 2));
        res.add(new Toilet(4, true, "Département STPI 1er étage", new GPSCoordinates(), 3));

        return res;
    }
}

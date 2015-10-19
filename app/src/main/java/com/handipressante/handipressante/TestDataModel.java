package com.handipressante.handipressante;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public class TestDataModel implements IDataModel {
    public List<Toilet> getToilets(GPSCoordinates ref, double latitudeRange, double longitudeRange) {
        ArrayList<Toilet> res = new ArrayList<>();
        res.add(new Toilet());

        return res;
    }
}

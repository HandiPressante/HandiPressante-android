package com.handipressante.handipressante;

import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public interface IDataModel {
    List<Toilet> getToilets(GPSCoordinates ref, double latitudeRange, double longitudeRange);
}

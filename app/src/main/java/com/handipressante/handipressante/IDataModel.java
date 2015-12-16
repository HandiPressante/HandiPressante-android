package com.handipressante.handipressante;

import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public interface IDataModel {
    List<Toilet> getToilets(GPSCoordinates ref, double xRange, double yRange);
    List<Toilet> getToilets(double lat_min, double lat_max, double long_min, double long_max);
}

package com.handipressante.handipressante;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public interface IDataModel {
    /**
     *
     * @param ref Current position
     * @param mincount Min count of toilets in the result
     * @param maxcount Max count of toilets in the result
     * @param distanceMax Max searching distance (in meters)
     * @return List of toilets
     */
    List<Toilet> getToiletsList(GeoPoint ref, int mincount, int maxcount, int distanceMax);

    /**
     *
     * @param topLeft Top left corner coordinates
     * @param bottomRight Bottom right corner coordinates
     * @return Toilets in the given rectangle
     */
    List<Toilet> getToiletsMap(GeoPoint topLeft, GeoPoint bottomRight);


    Toilet getToiletFromCache(int id);
}

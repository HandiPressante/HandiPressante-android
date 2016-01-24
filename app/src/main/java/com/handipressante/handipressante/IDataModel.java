package com.handipressante.handipressante;

import android.content.Context;

import org.osmdroid.util.GeoPoint;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by Nico on 19/10/2015.
 */
public interface IDataModel {
    /**
     * Add a listener which will be notified when a property change
     * @param listener The listener
     */
    void addChangeListener(PropertyChangeListener listener);

    /**
     * Check if a toilet with the given id exists in local data
     * @param id Id of the toilet to check
     * @return return true if so, else return false
     */
    boolean containsToilet(int id);

    /**
     * Retrieve nearby toilets from data source and store them in local data
     * @param ref Reference position
     * @param mincount Min count of toilets in the result
     * @param maxcount Max count of toilets in the result
     * @param distanceMax Max searching distance (in meters)
     */
    void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax);

    /**
     * Retrieve toilets in a given rectangle from data source and store them in local data
     * @param topLeft Top left corner coordinates
     * @param bottomRight Bottom right corner coordinates
     */
    void requestMapToilets(GeoPoint topLeft, GeoPoint bottomRight);

    /**
     * Retrieve toilet with the given id from data source and store it in local data
     * @param id Id of the toilet
     */
    void requestToilet(int id);

    /**
     * Return nearby toilets stored in local data
     * @return List of toilets
     */
    List<Toilet> getNearbyToilets();

    /**
     * Return toilets in a given rectangle stored in local data
     * @return Toilets in the given rectangle
     */
    List<Toilet> getMapToilets();

    /**
     *
     * @param geo Position where the toilet is being searched
     * @return The toilet at the position geo
     */
    Toilet getToilet(GeoPoint geo);

    /**
     * Return toilet with the given id stored in local data
     * @param id Id of a toilet
     * @return The toilet if exists, else null
     */
    Toilet getToilet(int id);
}

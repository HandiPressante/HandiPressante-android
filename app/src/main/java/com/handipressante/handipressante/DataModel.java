package com.handipressante.handipressante;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 02/02/2016.
 */

// TODO : Store all in local
public class DataModel {
    private static DataModel INSTANCE;
    public static synchronized DataModel instance() {
        if (INSTANCE == null) INSTANCE = new DataModel();
        return INSTANCE;
    }

    private List<Toilet> mToilets;
    private List<Toilet> mNearbyToilets;
    private List<Toilet> mMapToilets; // TODO : could be mToilets...

    private DataModel() {
        mToilets = new ArrayList<>();
        mNearbyToilets = new ArrayList<>();
        mMapToilets = new ArrayList<>();
    }

    /**
     * Return toilet with the given id stored in local data
     * @param id Id of a toilet
     * @return The toilet if exists, else null
     */
    public Toilet getToilet(int id){
        for (Toilet t : mToilets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Adds the given toilet if not in local data, else update local data
     * @param t Toilet
     */
    private void addToilet(Toilet t) {
        Toilet old = getToilet(t.getId());

        if (old != null) {
            old.updateData(t);
        } else {
            mToilets.add(t);
        }
    }

    // TODO : Check later if really needed
    /**
     * Adds given toilets if not in local data, else update local data
     * @param toilets Toilet list
     */
    private void addToilets(List<Toilet> toilets) {
        for (Toilet t : toilets) {
            addToilet(t);
        }
    }


    public interface NearbyToiletsListener {
        void onDataChanged();
    }

    private List<NearbyToiletsListener> mNearbyToiletsListeners = new ArrayList<>();

    public void addNearbyToiletsListener(NearbyToiletsListener listener) {
        mNearbyToiletsListeners.add(listener);
    }

    // TODO : Really needed ?
    public void removeNearbyToiletsListener(NearbyToiletsListener listener) {
        mNearbyToiletsListeners.remove(listener);
    }

    private void notifyNearbyToiletListeners() {
        for (NearbyToiletsListener listener : mNearbyToiletsListeners) {
            listener.onDataChanged();
        }
    }

    /**
     * Return nearby toilets stored in local data
     * @return List of toilets
     */
    public List<Toilet> getNearbyToilets() {
        return mNearbyToilets;
    }

    /**
     * Clear nearby toilet list
     */
    public void clearNearbyToilets() {
        mNearbyToilets.clear();
        notifyNearbyToiletListeners();
    }

    /**
     * Adds the given toilet in the list of nearby toilets
     * @param t Toilet
     */
    public void addNearbyToilet(Toilet t) {
        mNearbyToilets.add(t);
        addToilet(t);
        notifyNearbyToiletListeners();
    }


    public interface MapToiletsListener {
        void onDataChanged();
    }

    private List<MapToiletsListener> mMapToiletsListeners = new ArrayList<>();

    public void addMapToiletListener(MapToiletsListener listener) {
        mMapToiletsListeners.add(listener);
    }

    // TODO : Really needed ?
    public void removeMapToiletListener(MapToiletsListener listener) {
        mMapToiletsListeners.remove(listener);
    }

    private void notifyMapToiletListeners() {
        for (MapToiletsListener listener : mMapToiletsListeners) {
            listener.onDataChanged();
        }
    }

    /**
     * Return map toilets stored in local data
     * @return List of toilets
     */
    public List<Toilet> getMapToilets() {
        return mMapToilets;
    }

    /**
     * Clear map toilet list
     */
    public void clearMapToilets() {
        mMapToilets.clear();
        notifyMapToiletListeners();
    }

    /**
     * Return toilet with the given id stored in local data
     * @param id Id of a toilet
     * @return The toilet if exists, else null
     */
    public Toilet getMapToilet(int id){
        for (Toilet t : mMapToilets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Adds the given map toilet if not in local data, else update local data
     * @param t Toilet
     * @return true if not already in the list
     */
    private boolean addMapToilet(Toilet t) {
        Toilet old = getMapToilet(t.getId());

        if (old != null) {
            old.updateData(t);
            return false;
        } else {
            mMapToilets.add(t);
            return true;
        }
    }

    /**
     * Adds the given toilets in the list of map toilets
     * @param toiletList Toilet list
     */
    public void addMapToilets(List<Toilet> toiletList) {
        boolean toiletAdded = false;

        for (Toilet t : toiletList) {
            toiletAdded = addMapToilet(t) || toiletAdded;
            addToilet(t);
        }

        if (toiletAdded)
            notifyMapToiletListeners();
    }
}

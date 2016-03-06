package com.handipressante.handipressante;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 02/02/2016.
 */
public class DataManager {
    private static DataManager INSTANCE;
    public static synchronized DataManager instance(Context ctx) {
        if (INSTANCE == null) INSTANCE = new DataManager(ctx);
        return INSTANCE;
    }

    private Context mCtx;

    private DataManager(Context context) {
        mCtx = context;
    }

    /**
     * Retrieve nearby toilets from data source and store them in local data
     * @param ref Reference position
     * @param mincount Min count of toilets in the result
     * @param maxcount Max count of toilets in the result
     * @param distanceMax Max searching distance (in meters)
     */
    public void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax) {
        Log.i("DataManager", "requestNearbyToilets");
        String url = "http://handipressante.carbonkiwi.net/api.php/toilettesliste/" + ref.getLongitude() + "/" + ref.getLatitude() + "/" + mincount + "/" + maxcount + "/" + distanceMax;

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("DataManager", "Toilets arrived !");
                        DataModel dataModel = DataModel.instance();
                        DataFactory facto = new DataFactory();

                        dataModel.clearNearbyToilets();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Toilet t = facto.createToilet(response.getJSONObject(i));
                                if (t != null) {
                                    dataModel.addNearbyToilet(t);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        RequestManager.instance(mCtx).addToRequestQueue(jsObjRequest);
    }



    /**
     * Retrieve toilets in a given rectangle from data source and store them in local data
     * @param topLeft Top left corner coordinates
     * @param bottomRight Bottom right corner coordinates
     */
    public void requestMapToilets(GeoPoint topLeft, GeoPoint bottomRight) {
        Log.i("DataManager", "requestMapToilets");
        String url = "http://handipressante.carbonkiwi.net/api.php/toilettescarte/" + topLeft.getLongitude() + "/" + topLeft.getLatitude() + "/" + bottomRight.getLongitude() + "/" + bottomRight.getLatitude();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("DataManager", "Toilets arrived !");
                        DataModel dataModel = DataModel.instance();
                        DataFactory facto = new DataFactory();

                        List<Toilet> toilets = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Toilet t = facto.createToilet(response.getJSONObject(i));
                                if (t != null) {
                                    toilets.add(t);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dataModel.addMapToilets(toilets);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        RequestManager.instance(mCtx).addToRequestQueue(jsObjRequest);
    }
}

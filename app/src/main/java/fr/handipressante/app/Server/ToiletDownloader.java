package fr.handipressante.app.Server;

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

import fr.handipressante.app.Data.DataFactory;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 28/03/2016.
 */
public class ToiletDownloader extends Downloader {
    public ToiletDownloader(Context context) {
        super(context);
    }

    /**
     * Retrieve nearby toilets from data source and store them in local data
     * @param ref Reference position
     * @param mincount Min count of toilets in the result
     * @param maxcount Max count of toilets in the result
     * @param distanceMax Max searching distance (in meters)
     * @param listener Listener to receive the toilet list response
     */
    public void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax,
                                     final Listener<List<Toilet>> listener) {
        Log.i("ToiletDownloader", "requestNearbyToilets");
        String url = MyConstants.API_URL + "toilettesliste/" +
                ref.getLongitude() + "/" + ref.getLatitude() + "/" +
                mincount + "/" + maxcount + "/" + distanceMax;

        requestToilets(url, listener);
    }


    /**
     * Retrieve toilets in a given rectangle from data source and store them in local data
     * @param topLeft Top left corner coordinates
     * @param bottomRight Bottom right corner coordinates
     * @param listener Listener to receive the toilet list response
     */
    public void requestMapToilets(GeoPoint topLeft, GeoPoint bottomRight,
                                  final Listener<List<Toilet>> listener) {
        Log.i("ToiletDownloader", "requestMapToilets");
        String url = MyConstants.API_URL + "toilettescarte/" +
                topLeft.getLongitude() + "/" + topLeft.getLatitude() + "/" +
                bottomRight.getLongitude() + "/" + bottomRight.getLatitude();

        requestToilets(url, listener);
    }

    private void requestToilets(String url, final Listener<List<Toilet>> listener) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("ToiletDownloader", "Toilets arrived !");
                        DataFactory facto = new DataFactory();
                        List<Toilet> toiletList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Toilet t = facto.createToilet(response.getJSONObject(i));
                                if (t != null) {
                                    toiletList.add(t);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onResponse(toiletList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });

        RequestManager.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }
}

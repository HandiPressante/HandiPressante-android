package fr.handipressante.app.Server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.handipressante.app.Data.DataFactory;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.MyConstants;
import fr.handipressante.app.R;

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
                                JSONObject jsonObject = response.optJSONObject(i);
                                if (jsonObject == null) continue;

                                Toilet t = facto.createToilet(jsonObject);
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

    public void postToilet(Toilet toilet, boolean newToilet, final Listener<Boolean> listener) {
        Log.i("ToiletDownloader", "postToilet");
        String url;
        Map<String, String> params = new HashMap<>();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");
        params.put("uuid", uuid);

        if (!newToilet)
            params.put("toilet_id", toilet.getId().toString());

        params.put("toilet_name", toilet.getAddress());
        params.put("toilet_accessible", toilet.isAdapted().toString());
        params.put("toilet_description", toilet.getDescription());

        if (newToilet) {
            Double latitude = toilet.getCoordinates().getLatitude();
            Double longitude = toilet.getCoordinates().getLongitude();
            params.put("toilet_latitude", latitude.toString());
            params.put("toilet_longitude", longitude.toString());
        }

        if (newToilet) {
            url = MyConstants.API_URL + "toilet-add";
        } else {
            url = MyConstants.API_URL + "toilet-edit";
        }

        postJson(url, params, listener);
    }

    private void postJson(String url, final Map<String, String> params, final Listener<Boolean> listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.has("success"))
                            Log.i("ToiletDownloader", "success : " + response.getBoolean("success"));
                        if (response.has("error"))
                            Log.i("ToiletDownloader", "error : " + response.getString("error"));
                        if (response.has("data"))
                            Log.i("ToiletDownloader", "data : " + response.getString("data"));

                        if (response.has("success") && response.getBoolean("success")) {
                            listener.onResponse(true);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                listener.onResponse(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                error.printStackTrace();
                listener.onResponse(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        RequestManager.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }
}

package fr.handipressante.app.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Cache;
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
import java.util.List;

import fr.handipressante.app.data.DataFactory;
import fr.handipressante.app.data.Toilet;
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

    /**
     * Retrieve specified toilet
     * @param id Toilet ID
     * @param listener Listener to receive the toilet response
     */
    public void requestToilet(int id, final Listener<List<Toilet>> listener) {
        String url = MyConstants.API_URL + "toilet/" + id;
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

        String key = jsObjRequest.getCacheKey();
        Cache cache = RequestManager.getInstance(mContext).getRequestQueue().getCache();
        if (cache != null) {
            if (cache.get(key) != null) {
                cache.remove(key);
            }
        }

        RequestManager.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public void postToilet(Toilet toilet, boolean newToilet, final Listener<JSONObject> listener) {
        Log.i("ToiletDownloader", "postToilet");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");
            data.put("uuid", uuid);

            if (!newToilet)
                data.put("toilet_id", toilet.getId().toString());

            data.put("toilet_name", toilet.getAddress());
            data.put("toilet_accessible", toilet.isAdapted());
            data.put("toilet_description", toilet.getDescription());

            data.put("toilet_latitude", toilet.getCoordinates().getLatitude());
            data.put("toilet_longitude", toilet.getCoordinates().getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        if (newToilet) {
            url = MyConstants.API_URL + "toilet-add";
        } else {
            url = MyConstants.API_URL + "toilet-edit";
        }

        postJson(url, data, listener);
    }

    public void postToiletRate(Toilet toilet, int cleanliness, int facilities, int accessibility, final Listener<JSONObject> listener) {
        Log.i("ToiletDownloader", "postToiletRate");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

            data.put("uuid", uuid);
            data.put("toilet_id", toilet.getId().toString());

            data.put("toilet_cleanliness", cleanliness);
            data.put("toilet_facilities", facilities);
            data.put("toilet_accessibility", accessibility);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.API_URL + "toilet-rate";

        postJson(url, data, listener);
    }

    public void postToiletComment(Integer toiletId, String username, String content, final Listener<JSONObject> listener) {
        Log.i("ToiletDownloader", "postToiletComment");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

            data.put("uuid", uuid);
            data.put("toilet_id", toiletId.toString());

            data.put("username", username);
            data.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.API_URL + "toilet-add-comment";

        postJson(url, data, listener);
    }

    private void postJson(String url, JSONObject data, final Listener<JSONObject> listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.has("success")) {
                            Log.i("ToiletDownloader", "success : " + response.getBoolean("success"));
                        }

                        if (response.has("error")) {
                            Log.i("ToiletDownloader", "error : " + response.getString("error"));
                        }

                        listener.onResponse(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    listener.onResponse(null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onResponse(null);
            }
        });

        String key = jsonObjectRequest.getCacheKey();
        Cache cache = RequestManager.getInstance(mContext).getRequestQueue().getCache();
        if (cache != null) {
            if (cache.get(key) != null) {
                cache.remove(key);
            }
        }

        RequestManager.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }
}

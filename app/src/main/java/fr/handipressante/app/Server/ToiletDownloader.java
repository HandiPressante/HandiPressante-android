package fr.handipressante.app.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public void requestNearbyToilets(LatLng ref, int mincount, int maxcount, int distanceMax,
                                     final Listener<List<Toilet>> listener) {
        Log.i("ToiletDownloader", "requestNearbyToilets");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int accessibilityFilter = Integer.valueOf(sharedPrefs.getString("accessibility_filter", "2"));
        int feeFilter = Integer.valueOf(sharedPrefs.getString("fee_filter", "2"));

        String url = MyConstants.BASE_URL + "/toilets/get-nearby/" +
                ref.latitude + "/" + ref.longitude + "/" +
                mincount + "/" + maxcount + "/" + distanceMax + "/" +
                accessibilityFilter + "/" + feeFilter;


        requestToilets(url, listener);
    }


    /**
     * Retrieve toilets in a given rectangle from data source and store them in local data
     * @param topLeft Top left corner coordinates
     * @param bottomRight Bottom right corner coordinates
     * @param listener Listener to receive the toilet list response
     */
    public void requestMapToilets(LatLng topLeft, LatLng bottomRight,
                                  final Listener<List<Toilet>> listener) {
        Log.i("ToiletDownloader", "requestMapToilets");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int accessibilityFilter = Integer.valueOf(sharedPrefs.getString("accessibility_filter", "2"));
        int feeFilter = Integer.valueOf(sharedPrefs.getString("fee_filter", "2"));

        String url = MyConstants.BASE_URL + "/toilets/get-area/" +
                topLeft.latitude + "/" + topLeft.longitude + "/" +
                bottomRight.latitude + "/" + bottomRight.longitude + "/" +
                accessibilityFilter + "/" + feeFilter;

        requestToilets(url, listener);
    }

    /**
     * Retrieve specified toilet
     * @param id Toilet ID
     * @param listener Listener to receive the toilet response
     */
    public void requestToilet(int id, final Listener<List<Toilet>> listener) {
        String url = MyConstants.BASE_URL + "/toilets/get-" + id;
        requestToilets(url, listener);
    }

    private void requestToilets(String url, final Listener<List<Toilet>> listener) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray data;
                        try {
                            checkResponse(response);
                            data = getData(response);
                        } catch (ServerResponseException e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        DataFactory facto = new DataFactory();
                        List<Toilet> toiletList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            try {
                                JSONObject jsonObject = data.optJSONObject(i);
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
                        Toast.makeText(mContext, "Une erreur serveur est survenue.", Toast.LENGTH_LONG).show();
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

    public void postToilet(Toilet toilet, boolean newToilet, final Listener<Boolean> listener) {
        Log.i("ToiletDownloader", "postToilet");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");
            data.put("user_id", uuid);

            if (!newToilet)
                data.put("toilet_id", toilet.getId().toString());

            data.put("toilet_name", toilet.getName());
            data.put("toilet_adapted", toilet.isAdapted());
            data.put("toilet_charged", toilet.isCharged());
            data.put("toilet_description", toilet.getDescription());

            data.put("toilet_latitude", toilet.getPosition().latitude);
            data.put("toilet_longitude", toilet.getPosition().longitude);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.BASE_URL + "/toilets/save";
        postJson(url, data, listener);
    }

    public void postToiletRate(Toilet toilet, int cleanliness, int facilities, int accessibility, final Listener<Boolean> listener) {
        Log.i("ToiletDownloader", "postToiletRate");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

            data.put("user_id", uuid);
            data.put("toilet_id", toilet.getId().toString());

            data.put("toilet_cleanliness", cleanliness);
            data.put("toilet_facilities", facilities);
            data.put("toilet_accessibility", accessibility);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.BASE_URL + "/toilets/rate";

        postJson(url, data, listener);
    }

    public void postToiletComment(Integer toiletId, String username, String content, final Listener<Boolean> listener) {
        Log.i("ToiletDownloader", "postToiletComment");
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

            data.put("user_id", uuid);
            data.put("toilet_id", toiletId.toString());

            data.put("username", username);
            data.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.BASE_URL + "/toilets/comments/add";

        postJson(url, data, listener);
    }

    private void postJson(String url, JSONObject data, final Listener<Boolean> listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    checkResponse(response);
                    listener.onResponse(true);
                } catch (ServerResponseException e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    listener.onResponse(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Une erreur serveur est survenue.", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                listener.onResponse(false);
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

package fr.handipressante.app.server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.handipressante.app.MyConstants;

/**
 * Created by Nico on 27/03/2016.
 */
public class Downloader {
    public interface Listener<T> {
        void onResponse(T response);
    }
    protected Context mContext;

    public Downloader(Context context) {
        mContext = context;
    }

    protected JSONArray getData(JSONObject response) throws ServerResponseException {
        JSONArray data = response.optJSONArray("data");
        if (data == null) {
            throw new ServerResponseException();
        }

        return data;
    }

    protected void postJson(String url, JSONObject data, final Listener<Boolean> listener) {
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

    protected void checkResponse(JSONObject response) throws ServerResponseException {
        int version = response.optInt("version");
        if (!response.has("version")) {
            throw new ServerResponseException();
        }

        if (version > MyConstants.API_VERSION) {
            throw new ServerResponseException("Veuillez mette HandiPressante à jour.");
        }
        try {
            if (!response.getBoolean("success")) {
                String error = response.getString("errorText");
                throw new ServerResponseException(error);
            }
        } catch (JSONException e) {
            throw new ServerResponseException();
        }
    }
}

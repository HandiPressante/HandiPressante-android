package fr.handipressante.app.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.R;
import fr.handipressante.app.data.DataFactory;
import fr.handipressante.app.data.Photo;
import fr.handipressante.app.MyConstants;

public class PhotoDownloader extends Downloader {
    public PhotoDownloader(Context context) {
        super(context);
    }

    public void downloadPhotoList(Integer toiletId, final Listener<List<Photo>> listener) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

        String url = MyConstants.BASE_URL + "/toilets/pictures/list-" + toiletId + "/" + uuid;

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
                        List<Photo> photoList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            try {
                                Photo p = facto.createPhoto(data.getJSONObject(i));
                                if (p != null) {
                                    photoList.add(p);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        listener.onResponse(photoList);
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

    public void postPictureReport(Integer pictureId, final Listener<Boolean> listener) {
        String url;
        JSONObject data = new JSONObject();

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(mContext.getString(R.string.saved_uuid), "no-uuid");

            data.put("user_id", uuid);
            data.put("picture_id", pictureId.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null);
            return;
        }

        url = MyConstants.BASE_URL + "/toilets/pictures/report";

        postJson(url, data, listener);
    }
}

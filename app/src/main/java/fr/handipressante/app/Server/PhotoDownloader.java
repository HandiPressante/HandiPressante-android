package fr.handipressante.app.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.data.DataFactory;
import fr.handipressante.app.data.Photo;
import fr.handipressante.app.MyConstants;

public class PhotoDownloader extends Downloader {
    public PhotoDownloader(Context context) {
        super(context);
    }

    public void downloadPhotoList(Integer toiletId, final Listener<List<Photo>> listener) {
        String url = MyConstants.BASE_URL + "/toilets/pictures/list-" + toiletId;


        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("PhotoDownloader", "Photos arrived !");
                        DataFactory facto = new DataFactory();
                        List<Photo> photoList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Photo p = facto.createPhoto(response.getJSONObject(i));
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
}

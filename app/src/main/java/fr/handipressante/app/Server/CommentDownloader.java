package fr.handipressante.app.server;

import android.content.Context;
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

import fr.handipressante.app.data.DataFactory;
import fr.handipressante.app.data.Comment;
import fr.handipressante.app.MyConstants;

public class CommentDownloader extends Downloader {
    public CommentDownloader(Context context) {
        super(context);
    }

    public void downloadCommentList(Integer toiletId, final Listener<List<Comment>> listener) {
        String url = MyConstants.BASE_URL + "/toilets/comments/list-" + toiletId;


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
                        List<Comment> commentList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            try {
                                Comment c = facto.createComment(data.getJSONObject(i));
                                if (c != null) {
                                    commentList.add(c);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        listener.onResponse(commentList);
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
}

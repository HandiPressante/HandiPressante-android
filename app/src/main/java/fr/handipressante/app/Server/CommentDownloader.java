package fr.handipressante.app.Server;

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

import fr.handipressante.app.Data.DataFactory;
import fr.handipressante.app.Data.Comment;
import fr.handipressante.app.MyConstants;

public class CommentDownloader extends Downloader {
    public CommentDownloader(Context context) {
        super(context);
    }

    public void downloadCommentList(Integer toiletId, final Listener<List<Comment>> listener) {
        String url = MyConstants.API_URL + "toilet-comment-list/" + toiletId;


        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("PhotoDownloader", "Comments arrived !");
                        DataFactory facto = new DataFactory();
                        List<Comment> commentList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Comment c = facto.createComment(response.getJSONObject(i));
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

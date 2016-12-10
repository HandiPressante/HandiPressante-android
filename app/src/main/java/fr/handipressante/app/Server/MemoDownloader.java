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

import fr.handipressante.app.data.Memo;
import fr.handipressante.app.data.DataFactory;
import fr.handipressante.app.MyConstants;

public class MemoDownloader extends Downloader {
    public MemoDownloader(Context context) {
        super(context);
    }

    public void downloadMemoList(final Listener<List<Memo>> listener) {
        String url = MyConstants.BASE_URL + "/memo/list";


        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("MemoDownloader", "Memos arrived !");
                        DataFactory facto = new DataFactory();
                        List<Memo> memoList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Memo m = facto.createMemo(response.getJSONObject(i));
                                if (m != null) {
                                    memoList.add(m);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        listener.onResponse(memoList);
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

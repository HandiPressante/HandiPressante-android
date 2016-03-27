package fr.handipressante.app.Server;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.Data.Memo;
import fr.handipressante.app.DataFactory;
import fr.handipressante.app.MyConstants;

public class MemoDownloader extends Downloader {
    private Context mContext;

    public MemoDownloader(Context context) {
        mContext = context;
    }

    public void downloadMemoList(final Listener<List<Memo>> listener) {
        String url = MyConstants.API_URL + "memos-list";


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

                            /*
                            MemoDAO memoDAO = new MemoDAO(mContext);
                            memoDAO.open();
                            memoDAO.save(memos);
                            memoDAO.close();
                            */

                        listener.onResponse(memoList);
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

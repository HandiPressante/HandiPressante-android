package fr.handipressante.app.Service;

import android.app.Service;
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
import fr.handipressante.app.Data.MemoDAO;
import fr.handipressante.app.DataFactory;
import fr.handipressante.app.MyConstants;
import fr.handipressante.app.RequestManager;

public class MemoDownloaderService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DownloadTask extends AsyncTask<URL, Void, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {
            String url = MyConstants.API_URL + "memos-update/2016-03-20 15:00:00";


            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (url, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Log.i("DataManager", "Memos arrived !");
                            DataFactory facto = new DataFactory();
                            List<Memo> memos = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    Memo m = facto.createMemo(response.getJSONObject(i));
                                    if (m != null) {
                                        memos.add(m);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            MemoDAO memoDAO = new MemoDAO(getApplicationContext());
                            memoDAO.open();
                            memoDAO.save(memos);
                            memoDAO.close();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            error.printStackTrace();
                        }
                    });

            RequestManager.instance(getApplicationContext()).addToRequestQueue(jsObjRequest);

            return true;
        }
    }


}

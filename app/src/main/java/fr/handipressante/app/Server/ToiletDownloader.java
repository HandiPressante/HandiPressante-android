package fr.handipressante.app.Server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.Data.DataFactory;
import fr.handipressante.app.DataModel;
import fr.handipressante.app.Data.Toilet;

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
     */
    public void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax,
                                     final Listener<List<Toilet>> listener) {
        Log.i("ToiletDownloader", "requestNearbyToilets");
        String url = "http://handipressante.carbonkiwi.net/api.php/toilettesliste/" + ref.getLongitude() + "/" + ref.getLatitude() + "/" + mincount + "/" + maxcount + "/" + distanceMax;

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("ToiletDownloader", "Toilets arrived !");
                        DataModel dataModel = DataModel.instance();
                        DataFactory facto = new DataFactory();
                        List<Toilet> toiletList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Toilet t = facto.createToilet(response.getJSONObject(i));
                                if (t != null) {
                                    toiletList.add(t);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onResponse(toiletList);

                        /*
                        dataModel.clearNearbyToilets();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Toilet t = facto.createToilet(response.getJSONObject(i));
                                if (t != null) {
                                    dataModel.addNearbyToilet(t);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        */
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

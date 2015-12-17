package com.handipressante.handipressante;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class OnlineDataModel implements IDataModel {
    private Context mContext;

    public OnlineDataModel(Context context) {
        mContext = context;
    }

    public List<Toilet> getToilets(double lat_min, double lat_max, double long_min, double long_max){
        ArrayList<Toilet> res = new ArrayList<>();
        return res;
    }



    public List<Toilet> getToilets(GPSCoordinates ref, double xRange, double yRange) {
        String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/" + ref.getL93X() + "/" + ref.getL93Y() + "/" + (xRange/2) + "/" + (yRange/2);

        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                InputStream is = downloadUrl(strUrl);
                DataFactory facto = new DataFactory();
                List<Toilet> res = facto.createToilets(is);
                is.close();
                return res;
            } catch (IOException e) {
                Log.e("Debug", "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
            }
        } else {
            Log.e("DataModel", "No network");
        }


        return new ArrayList<Toilet>();
    }

    public Sheet getSheet(int id) {
        String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/fiches/" + String.valueOf(id);
        //String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/fiches/80";
        Log.d("URL : ", strUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        System.out.println("NetworkInfo end");

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                System.out.println("Lecture du stream");
                InputStream is = downloadUrl(strUrl);
                System.out.println("Sheet Downloaded");
                DataFactory facto = new DataFactory();
                Sheet res = facto.createSheet(is);
                Log.d("Sheet downloaded", res.get_name());
                is.close();
                return res;
            } catch (IOException e) {
                Log.e("Debug", "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
            }
        } else {
            Log.e("DataModel", "No network");
        }


        // If not, return default Sheet
        return new Sheet();
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private InputStream downloadUrl(String myurl) throws IOException {

        InputStream is = null;
        ByteArrayInputStream res = null;

        try {
            System.out.println("downloadURL 1");
            URL url = new URL(myurl);
            Log.d("downloadURL 2", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d("downloadURL 3", conn.toString());
            conn.setReadTimeout(10000 /* milliseconds */);
            Log.d("downloadURL 4", conn.toString());
            conn.setConnectTimeout(15000 /* milliseconds */);
            Log.d("downloadURL 5", conn.toString());
            conn.setRequestMethod("GET");
            Log.d("downloadURL 6", conn.toString());
            conn.setDoInput(true);
            Log.d("downloadURL 7", conn.toString());
            // Starts the query
            conn.connect();
            Log.d("downloadURL 8", conn.toString());
            int response = conn.getResponseCode();
            Log.d("downloadURL 9", conn.toString());
            Log.d("Debug", "The response is: " + response);
            Log.d("downloadURL 10", conn.toString());
            is = conn.getInputStream();
            Log.d("downloadURL 11", is.toString());

        } finally {
            /*if (is != null) {
                is.close();
            }*/
        }
        System.out.println("downloadURL 12");
        return is;
    }
}

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

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private InputStream downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        ByteArrayInputStream res = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Debug", "The response is: " + response);
            is = conn.getInputStream();

            /*DataFactory facto = new DataFactory();

            // TODO : search for good size
            byte[] buffer = new byte[2048*2048];
            is.read(buffer);

            res = new ByteArrayInputStream(buffer);*/

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            /*if (is != null) {
                is.close();
            }*/
        }

        return is;
    }
}

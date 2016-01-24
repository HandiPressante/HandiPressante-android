package com.handipressante.handipressante;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.osmdroid.util.GeoPoint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OnlineDataModel implements IDataModel {
    private static OnlineDataModel _instance = null;

    private List<PropertyChangeListener> _listeners = new ArrayList<>();
    private Context _context;
    private List<Toilet> _toilets = new ArrayList<>();
    private List<Toilet> _nearbyToilets = new ArrayList<>();
    private List<Toilet> _mapToilets = new ArrayList<>();

    public static OnlineDataModel instance(Context context) {
        if (_instance == null) _instance = new OnlineDataModel(context);
        return _instance;
    }

    public static OnlineDataModel instance() throws Exception {
        if (_instance == null) throw new Exception("Context not initialized");
        return _instance;
    }

    private OnlineDataModel(Context context) {
        _context = context;
    }

    public boolean containsToilet(int id) {
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) return true;
        }

        return false;
    }

    @Override
    public void requestNearbyToilets(GeoPoint ref, int mincount, int maxcount, int distanceMax) {
        OnlineRequest request = new NearbyToiletsRequest(ref, mincount, maxcount, distanceMax);
        new DownloadToiletsTask().execute(request);
    }

    @Override
    public void requestMapToilets(GeoPoint topLeft, GeoPoint bottomRight) {
        OnlineRequest request = new MapToiletsRequest(topLeft, bottomRight);
        new DownloadToiletsTask().execute(request);
    }

    @Override
    public void requestToilet(int id) {

    }

    @Override
    public Toilet getToilet(int id){
        for (Toilet t : _toilets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public List<Toilet> getNearbyToilets() {
        return _nearbyToilets;
    }

    @Override
    public List<Toilet> getMapToilets() {
        return _mapToilets;
    }

    /**
     * Adds the given toilet if not in local data, else update local data
     * @param t Toilet
     */
    private void addToilet(Toilet t) {
        Toilet old = getToilet(t.getId());

        if (old != null) {
            old.updateData(t);
        } else {
            _toilets.add(t);
        }
    }

    /**
     * Adds given toilets if not in local data, else update local data
     * @param toilets Toilet list
     */
    private void addToilets(List<Toilet> toilets) {
        for (Toilet t : toilets) {
            addToilet(t);
        }
    }

    // TODO : necessary ?
    private void requestToilets(GPSCoordinates ref, double xRange, double yRange) {
        /*
        String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/" + ref.getL93X() + "/" + ref.getL93Y() + "/" + (xRange/2) + "/" + (yRange/2);

        ConnectivityManager connMgr = (ConnectivityManager)
                _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                InputStream is = downloadUrl(strUrl);
                DataFactory facto = new DataFactory();
                List<Toilet> res = facto.createToilets(is);
                is.close();
            } catch (IOException e) {
                Log.e("Debug", "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
            }
        } else {
            Log.e("DataModel", "No network");
        }
        */
    }

    public Sheet getSheet(int id) {
        /*
        String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/fiches/" + String.valueOf(id);
        //String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/fiches/80";
        Log.d("URL : ", strUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        System.out.println("NetworkInfo end");

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                System.out.println("Lecture du stream");
                InputStream is = downloadUrl(strUrl);
                System.out.println("Sheet Downloaded");
                DataFactory facto = new DataFactory();
                Sheet res = facto.createSheet(is);
                System.out.println("Sheet build");
                Log.d("Sheet downloaded", res.get_name());
                is.close();
                return res;
            } catch (IOException e) {
                Log.e("Debug", "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
            }
        } else {
            Log.e("DataModel", "No network");
        }*/


        // If not, return default Sheet
        return new Sheet();
    }



    // TODO : Remove
    public Toilet getToilet(GeoPoint geo) {
        for (Toilet t : _toilets) {
            if (t.getGeo().equals(geo)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
        _listeners.add(listener);
    }

    private void notifyListeners(Object o, String property, Object oldValue, Object newValue) {
        for (PropertyChangeListener listener : _listeners) {
            listener.propertyChange(new PropertyChangeEvent(o, property, oldValue, newValue));
        }
    }


    private class NearbyToiletsRequest extends OnlineRequest {
        public NearbyToiletsRequest(GeoPoint ref, int mincount, int maxcount, int distanceMax) {
            super("http://handipressante.carbonkiwi.net/api.php/toilettesliste/" + ref.getLongitude() + "/" + ref.getLatitude() + "/" + mincount + "/" + maxcount + "/" + distanceMax);
        }

        @Override
        public void postExecute() {
            try {
                DataFactory facto = new DataFactory();
                List<Toilet> toilets = facto.createToilets(_data);

                addToilets(toilets);
                notifyListeners(this, "NearbyToilets", _nearbyToilets, _nearbyToilets = toilets);
            } catch (java.io.IOException e) {
                Log.e("Error", "Nearby toilets request IOExecption");
                e.printStackTrace();
            }
        }
    }


    private class MapToiletsRequest extends OnlineRequest {
        public MapToiletsRequest(GeoPoint topLeft, GeoPoint bottomRight) {
            super("http://handipressante.carbonkiwi.net/api.php/toilettescarte/" + bottomRight.getLongitude() + "/" + bottomRight.getLatitude() + "/" + topLeft.getLongitude() + "/" + topLeft.getLatitude());
        }

        @Override
        public void postExecute() {
            try {
                DataFactory facto = new DataFactory();
                List<Toilet> toilets = facto.createToilets(_data);

                addToilets(toilets);
                notifyListeners(this, "MapToilets", _mapToilets, _mapToilets = toilets);
            } catch (java.io.IOException e) {
                Log.e("Error", "Map toilets request IOExecption");
                e.printStackTrace();
            }
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadToiletsTask extends AsyncTask<OnlineRequest, Void, OnlineRequest> {
        @Override
        protected OnlineRequest doInBackground(OnlineRequest... params) {
            if (_context == null) {
                Log.e("Debug", "No context set");
                return null;
            }

            if (params.length == 0) return null;
            OnlineRequest req = params[0];
            if (req == null) return null;

            ConnectivityManager connMgr = (ConnectivityManager)
                    _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    String data = downloadUrl(req.getUrl());
                    req.setData(data);
                    return req;
                } catch (IOException e) {
                    Log.e("Debug", "Unable to retrieve web page. URL may be invalid.");
                    e.printStackTrace();
                }
            } else {
                Log.e("DataModel", "No network");
            }

            System.out.println("problem");
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(OnlineRequest request) {
            if (request == null) {
                Log.e("Debug", "No request");
                return;
            }

            request.postExecute();
        }
        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {

            InputStream is = null;
            String res = "";

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

                res = IOUtils.toString(is, "UTF-8");
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            System.out.println("downloadURL 12");

            return res;
        }
    }
}

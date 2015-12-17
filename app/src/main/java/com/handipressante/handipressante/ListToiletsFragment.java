package com.handipressante.handipressante;


/**
 * Created by Tom on 21/10/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import org.osmdroid.util.GeoPoint;

public class ListToiletsFragment extends ListFragment {

    private List<Toilet> listOfToilets = new ArrayList<Toilet>();
    private LocationManager _locationManager;
    private LocationListener _locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        /*
        System.out.println("Nico onActivityCreated");

        _locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Log.d("Nico", "onActivityCreated");
        if (_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            System.out.println("Nico GPS Provider found");
            Log.d("Nico", "GPS Provider found");
            _locationListener = new MyLocationListener();
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, _locationListener);
            _locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, _locationListener, null);
            System.out.println("Nico GPS Updates requested");
            Log.d("Nico", "GPS Updates requested");

        } else {
            Log.e("Nico", "GPS Provider not found");
            System.out.println("Nico GPS Provider not found");
        }
        */

        // GPSCoordinate of user ###### NEEDS TO BE DYNAMIC ########
        //GPSCoordinates myPlace = new GPSCoordinates(351861.03, 6789173.05);
        GeoPoint myPlace = new GeoPoint(48.12079, -1.63440);

        // Download toilets
        new DownloadToiletsTask().execute(myPlace);

        // Default toilet in case of error
        listOfToilets.add(0, new Toilet(123456, false, "Pas de toilette", new GeoPoint(0,0), 0.0));

        // Generation of view
        generateView();

        // Add ClickListener
        final ListView toiletList = this.getListView();
        toiletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Show new activity when clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position touched", String.valueOf(position));
                Log.d("id touched", String.valueOf(id));
                Toilet t = (Toilet) listOfToilets.get(position);
                Log.d("Toilet touched", t.getAddress());
                Integer idSheet = t.getId();
                if (idSheet != -1) // If it's not the default toilet
                {
                    Intent intent = new Intent(getActivity(), ToiletSheetActivity.class);
                    Bundle b = new Bundle();
                    Log.d("Id send to the sheet", String.valueOf(idSheet));
                    b.putInt("idSheet", idSheet);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }



    public void generateView(){
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<this.listOfToilets.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", this.listOfToilets.get(i).getAddress());
            Integer averageRank = listOfToilets.get(i).getRankAverage();
            hm.put("rank",Integer.toString(this.listOfToilets.get(i).getRankIcon(averageRank)));
            hm.put("icon_pmr", Integer.toString(this.listOfToilets.get(i).getIcon()));
            hm.put("dist", this.listOfToilets.get(i).getDistanceToString());
            aList.add(hm);
        }
        // Keys used in Hashmap
        String[] from = { "icon_pmr","txt", "dist" ,"rank"};
        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.dist,R.id.rank};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.fragment_list, from, to);
        setListAdapter(adapter);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadToiletsTask extends AsyncTask<GeoPoint, Void, List<Toilet>> {
        @Override
        protected List<Toilet> doInBackground(GeoPoint... params) {
            //IDataModel model = new TestDataModel();
            IDataModel model = OnlineDataModel.instance(getContext());
            return model.getToiletsList(params[0], 5, 20, 100000);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Toilet> result) {

            for (Toilet t : result) {
                System.out.println("Toilette : " + t.getId());
                Log.d("Debug", "########################");
                Log.d("Debug", "Toilette " + t.getId());
                Log.d("Debug", "Adresse : " + t.getAddress());
                Log.d("Debug", "PMR : " + t.isAdapted());
                Log.d("Debug", "Lat : " + t.getCoordinates().getLatitude());
                Log.d("Debug", "Long : " + t.getCoordinates().getLongitude());
                Log.d("Debug", "########################");
            }

            listOfToilets = result;
            generateView();
            //textView.setText(result);
        }


    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            System.out.println("Nico : " + longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            System.out.println("Nico : " + latitude);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}


package com.handipressante.handipressante;


/**
 * Created by Tom on 21/10/2015.
 */

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

import org.osmdroid.util.GeoPoint;

public class ListToiletsFragment extends ListFragment implements PropertyChangeListener {
    private IDataModel _model;
    private List<Toilet> listOfToilets = new ArrayList<Toilet>();
    private LocationManager _locationManager;
    private LocationListener _locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        System.out.println("Nico onActivityCreated");

        //_model = new TestDataModel();
        try {
            _model = OnlineDataModel.instance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        _model.addChangeListener(this);
       /* _model = OnlineDataModel.instance();
        _model.setContext(getContext());*/

        System.out.println("Nico onActivityCreated");

        _locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        Log.d("Nico", "onActivityCreated");
        if (_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            System.out.println("Nico GPS Provider found");
            Log.d("Nico", "GPS Provider found");
            _locationListener = new MyLocationListener();
            try {
                _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, _locationListener);
                _locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, _locationListener, null);
                System.out.println("Nico GPS Updates requested");
                Log.d("Nico", "GPS Updates requested");
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        } else if (_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            System.out.println("Nico Network Provider found");
            Log.d("Nico", "Network Provider found");
            _locationListener = new MyLocationListener();
            try {
                _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, _locationListener);
                _locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, _locationListener, null);
                System.out.println("Nico Network Updates requested");
                Log.d("Nico", "Network Updates requested");
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }  else {
            Log.e("Nico", "GPS Provider not found");
            System.out.println("Nico GPS Provider not found");
        }

        // GPSCoordinate of user ###### NEEDS TO BE DYNAMIC ########
        //GPSCoordinates myPlace = new GPSCoordinates(351861.03, 6789173.05);

        listOfToilets = new ArrayList<>();
        // Default toilet in case of error
        listOfToilets.add(0, new Toilet(123456, false, "Pas de toilette", new GeoPoint(0, 0), 0.0));
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

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getNewValue() == null) return;

        System.out.println("Nico property changed");
        if (event.getPropertyName().equals("NearbyToilets")) {
            System.out.println("Nico update toilet list");
            listOfToilets = (List<Toilet>) event.getNewValue();
            System.out.println("Nico generate view");
            generateView();
        }
    }



    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            System.out.println("Nico : " + longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            System.out.println("Nico : " + latitude);

            GeoPoint myPlace = new GeoPoint(loc.getLatitude(), loc.getLongitude());
            // Download toilets
            _model.requestNearbyToilets(myPlace, 5, 20, 100000);
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("Nico : onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("Nico : onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("Nico : onStatusChanged");
        }
    }
}


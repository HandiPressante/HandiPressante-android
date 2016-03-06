package com.handipressante.handipressante;


/**
 * Created by Tom on 21/10/2015.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;


public class ToiletListFragment extends ListFragment implements LocationListener {
    private LocationManager mLocationManager;
    private String mProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("ToiletListFragment", "onActivityCreated");

        ToiletListAdapter adapter = new ToiletListAdapter(getActivity(), DataModel.instance().getNearbyToilets());
        DataModel.instance().addNearbyToiletsListener(adapter);
        setListAdapter(adapter);

        // Get the location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, true);
        Log.i("ToiletListFragment", "Provider selected : " + mProvider);
        Toast.makeText(getContext(), "Provider selected : " + mProvider,
                Toast.LENGTH_SHORT).show();
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("ToiletListFragment", "onResume");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (DataModel.instance().getNearbyToilets().size() == 0) {
            Location location = mLocationManager.getLastKnownLocation(mProvider);

            if (location != null) {
                Toast.makeText(getContext(), "Provider " + mProvider + " has a last known location.",
                        Toast.LENGTH_SHORT).show();
                onLocationChanged(location);
            } else {
                Toast.makeText(getContext(), "Location not available",
                        Toast.LENGTH_SHORT).show();
                mLocationManager.requestSingleUpdate(mProvider, this, null);
                Log.i("ToiletListFragment", "requestSingleUpdate");
            }
        }

        try {
            // TODO : Do something with the return value
            startLocationUpdates();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        Log.i("ToiletListFragment", "onPause");

        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean startLocationUpdates() throws SecurityException {
        boolean result = false;
        for (final String provider : mLocationManager.getProviders(true)) {
            // TODO : Better parameters ?
            mLocationManager.requestLocationUpdates(provider, 10*1000, 5.0f, this);
            result = true;
        }
        return result;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toilet t = (Toilet) getListAdapter().getItem(position);
        if (t != null) {
            Intent intent = new Intent(getActivity(), ToiletSheetActivity.class);
            Bundle b = new Bundle();
            b.putInt("toiletId", t.getId());
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("ToiletListFragment", "onLocationChanged");

        /*
        Toast.makeText(getContext(), "New Location (" + location.getLatitude() + "," + location.getLongitude() + ", provider : " + location.getProvider() + ", accuracy : " + location.getAccuracy(),
                Toast.LENGTH_SHORT).show();
                */

        // currentLocation : new GeoPoint(48.12063, -1.63447);
        // TODO : Memorize location
        // TODO : Compare prev and new location and decide if network request really needed
        DataManager.instance(getActivity().getApplicationContext()).requestNearbyToilets(new GeoPoint(location.getLatitude(), location.getLongitude()), 5, 20, 2000);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getContext(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getContext(), "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
        DataModel.instance().clearNearbyToilets();
    }
}


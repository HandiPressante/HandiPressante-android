package fr.handipressante.app;


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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.Data.NearbyToiletDAO;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;


public class ToiletListFragment extends ListFragment implements LocationListener {
    private LocationManager mLocationManager;
    private String mProvider;
    private GeoPoint mCurrentGeopoint;

    private ToiletListAdapter mAdapter;
    private List<Toilet> mToiletCache;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("ToiletListFragment", "onActivityCreated");

        mToiletCache = new ArrayList<>();
        mAdapter = new ToiletListAdapter(getActivity(), mToiletCache);
        //mAdapter = new ToiletListAdapter(getActivity(), DataModel.instance().getNearbyToilets());
        //DataModel.instance().addNearbyToiletsListener(adapter);

        // Get the location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, true);
        Log.i("ToiletListFragment", "Provider selected : " + mProvider);
        Toast.makeText(getContext(), "Provider selected : " + mProvider,
                Toast.LENGTH_SHORT).show();

        if (savedInstanceState != null) {
            mCurrentGeopoint = savedInstanceState.getParcelable("current_location");
        }
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("ToiletListFragment", "onResume");

        new LoadDatabaseTask().execute();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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

        try {
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

        if (!mToiletCache.isEmpty()) {
            NearbyToiletDAO dao = new NearbyToiletDAO(getContext());
            dao.open();
            dao.truncate();
            dao.addAll(mToiletCache);
            dao.close();
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);

        if (mCurrentGeopoint != null)
            outState.putParcelable("current_location", mCurrentGeopoint);

        Log.i("ToiletListFragment", "Instance state saved");
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
            intent.putExtra("toilet", t);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("ToiletListFragment", "onLocationChanged");
        mCurrentGeopoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (!mToiletCache.isEmpty()) {
            updateToiletList(mToiletCache, false);
        }

        ToiletDownloader downloader = new ToiletDownloader(getContext());
        downloader.requestNearbyToilets(new GeoPoint(location.getLatitude(), location.getLongitude()),
                5, 20, 2000, new Downloader.Listener<List<Toilet>>() {
                    @Override
                    public void onResponse(List<Toilet> response) {
                        updateToiletList(response, false);
                    }
                });
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
        //DataModel.instance().clearNearbyToilets();
    }

    synchronized private void updateToiletList(List<Toilet> toiletList, boolean firstOnly) {
        if (!firstOnly || getListAdapter() == null) {
            for (Toilet t : toiletList) {
                t.setDistanceWith(mCurrentGeopoint);
            }
            Toilet.sortListByDistance(toiletList);

            mAdapter.swapItems(toiletList);
            mToiletCache = toiletList;

            if (mCurrentGeopoint != null) {
                setListAdapter(mAdapter);
            }
        }
    }

    private class LoadDatabaseTask extends AsyncTask<Void, Void, List<Toilet>> {
        @Override
        protected List<Toilet> doInBackground(Void... params) {
            NearbyToiletDAO dao = new NearbyToiletDAO(getContext());
            dao.open();
            List<Toilet> result = dao.selectAll();
            dao.close();

            return result;
        }

        @Override
        protected void onPostExecute(List<Toilet> toiletList) {
            if (!toiletList.isEmpty()) {
                updateToiletList(toiletList, true);
            }
        }
    }
}


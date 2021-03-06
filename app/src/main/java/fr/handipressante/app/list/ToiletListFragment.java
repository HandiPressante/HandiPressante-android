package fr.handipressante.app.list;


/**
 * Created by Tom on 21/10/2015.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.data.NearbyToiletDAO;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.help.HelpSlideList;
import fr.handipressante.app.R;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;
import fr.handipressante.app.show.ToiletSheetActivity;

public class ToiletListFragment extends ListFragment implements LocationListener {

    private final String LOG_TAG = "ToiletListFragment";

    private LocationManager mLocationManager;
    private String mProvider;
    private LatLng mCurrentLatLng;

    private ToiletListAdapter mAdapter;
    private List<Toilet> mToiletCache;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toiletlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActivityCreated");

        Toolbar toolbarBottom = (Toolbar) getActivity().findViewById(R.id.toolbar_scroll);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean scroll_help = sharedPrefs.getBoolean("scroll_help", false);

        // Activate help menu
        setHasOptionsMenu(true);

        if(!scroll_help) {
            toolbarBottom.setVisibility(View.GONE);
        }

        getActivity().findViewById(R.id.upList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                getListView().smoothScrollByOffset(-offset);
            }
        });
        getActivity().findViewById(R.id.downList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                getListView().smoothScrollByOffset(offset);
            }
        });

        mToiletCache = new ArrayList<>();
        mAdapter = new ToiletListAdapter(getContext().getApplicationContext(), mToiletCache);
        //mAdapter = new ToiletListAdapter(getActivity(), DataModel.instance().getNearbyToilets());
        //DataModel.instance().addNearbyToiletsListener(adapter);

        // Get the location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, true);
        Log.i("ToiletListFragment", "Provider selected : " + mProvider);
        //Toast.makeText(getContext(), "Provider selected : " + mProvider,
        //        Toast.LENGTH_SHORT).show();

        if (savedInstanceState != null) {
            mCurrentLatLng = savedInstanceState.getParcelable("current_location");
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
            //Toast.makeText(getContext(), "Provider " + mProvider + " has a last known location.",
            //        Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {
            //Toast.makeText(getContext(), "Location not available",
            //        Toast.LENGTH_SHORT).show();
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

        if (mCurrentLatLng != null)
            outState.putParcelable("current_location", mCurrentLatLng);

        Log.i("ToiletListFragment", "Instance state saved");
    }

    private int computeVisibleItemCount() {
        if (mToiletCache.isEmpty())
            return 0;

        int listViewHeight = getListView().getHeight();
        View listItem = mAdapter.getView(0, null, getListView());
        listItem.measure(0, 0);
        int childItemHeight = listItem.getMeasuredHeight() + getListView().getDividerHeight();

        return listViewHeight / childItemHeight;
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
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!mToiletCache.isEmpty()) {
            updateToiletList(mToiletCache, false);
        }

        ToiletDownloader downloader = new ToiletDownloader(getContext());
        downloader.requestNearbyToilets(new LatLng(location.getLatitude(), location.getLongitude()),
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
        //Toast.makeText(getContext(), "Enabled new provider " + provider,
        //        Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(getContext(), "Disabled provider " + provider,
        //        Toast.LENGTH_SHORT).show();
        //DataModel.instance().clearNearbyToilets();
    }

    synchronized private void updateToiletList(List<Toilet> toiletList, boolean firstOnly) {
        if (!firstOnly || getListAdapter() == null) {
            for (Toilet t : toiletList) {
                t.setDistanceWith(mCurrentLatLng);
            }
            Toilet.sortListByDistance(toiletList);

            mAdapter.swapItems(toiletList);
            mToiletCache = toiletList;

            if (mCurrentLatLng != null) {
                setListAdapter(mAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intentHelp = new Intent(getContext(), HelpSlideList.class);
                startActivity(intentHelp);
                break;
        }
        return super.onOptionsItemSelected(item);
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


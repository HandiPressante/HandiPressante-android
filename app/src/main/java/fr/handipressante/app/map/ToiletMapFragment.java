package fr.handipressante.app.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.help.HelpSlideMap;
import fr.handipressante.app.R;
import fr.handipressante.app.edit.AddToiletDialog;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;
import fr.handipressante.app.show.ToiletSheetActivity;

public class ToiletMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener, ClusterManager.OnClusterItemClickListener<Toilet>, ClusterManager.OnClusterItemInfoWindowClickListener<Toilet>, GoogleMap.OnMapClickListener {

    private final String LOG_TAG = "ToiletMapFragment";

    private boolean mAccessibilityOptionEnabled = true;

    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mMapReady = false;
    private CameraPosition mSavedCameraPosition = null;

    private final float MIN_ZOOM_TO_ADD_TOILET = 19.0f;
    private boolean mAddToiletEnabled = false;
    private boolean mToiletCreationReady = false;
    private boolean mToiletCreationRequested = false;
    final int REQUEST_ADD_TOILET = 1;

    private final float MIN_ZOOM_TO_SHOW_TOILET = 11.0f;
    private LatLng mExtNorthEast;
    private LatLng mExtSouthWest;

    final int REQUEST_TOILET_SHEET = 2;


    private Toilet lastMarkerClicked = null;
    private ClusterManager<Toilet> mClusterManager;
    private ToiletRenderer mToiletRenderer;
    private Map<Integer, Toilet> mDownloadedToilets = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toiletmap, container, false);

        // Gets the MapView from the XML layout and creates it
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Activate help menu
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mAccessibilityOptionEnabled = sharedPrefs.getBoolean("scroll_help", false);

        if (mAccessibilityOptionEnabled) {
            initAccessibilityLayout();
        }

        if (sharedPrefs.contains("saved_latitude") &&
                sharedPrefs.contains("saved_longitude") &&
                sharedPrefs.contains("saved_zoom") &&
                sharedPrefs.contains("saved_bearing")) {

            double savedLatitude = sharedPrefs.getFloat("saved_latitude", 0);
            double savedLongitude = sharedPrefs.getFloat("saved_longitude", 0);
            float savedZoom = sharedPrefs.getFloat("saved_zoom", 21);
            float savedBearing = sharedPrefs.getFloat("saved_bearing", 0);

            mSavedCameraPosition = CameraPosition.builder()
                    .target(new LatLng(savedLatitude, savedLongitude))
                    .zoom(savedZoom)
                    .bearing(savedBearing)
                    .build();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mToiletCreationReady = true;
    }

    @Override
    public void onPause() {
        saveCameraPosition();

        super.onPause();

        if (mAddToiletEnabled) {
            mAddToiletEnabled = false;
            getActivity().supportInvalidateOptionsMenu();
        }
        mToiletCreationReady = false;
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        saveCameraPosition();

        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intentHelp = new Intent(getContext(), HelpSlideMap.class);
                startActivity(intentHelp);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mToiletRenderer = new ToiletRenderer(getContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(mToiletRenderer);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(new ToiletInfoWindowAdapter());

        if (mAccessibilityOptionEnabled) {
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            // todo
        }

        mMapReady = true;

        if (mSavedCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mSavedCameraPosition));
        }

        mClusterManager.addItems(mDownloadedToilets.values());
        mClusterManager.cluster();

        if (mToiletCreationRequested) {
            mToiletCreationRequested = false;
            onToiletCreationRequested();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_TOILET && resultCode == 0 && data != null) {
            Toilet toilet = data.getParcelableExtra("toilet");
            if (toilet != null) {
                mDownloadedToilets.put(toilet.getId(), toilet);
                mClusterManager.addItem(toilet);
                mClusterManager.cluster();

                // Reset "add toilet button" state
                mAddToiletEnabled = false;
                getActivity().supportInvalidateOptionsMenu();
            }
        } else if (requestCode == REQUEST_TOILET_SHEET && resultCode == 0 && data != null) {
            Toilet toilet = data.getParcelableExtra("toilet");
            if (toilet != null) {
                Toilet currentToilet = mDownloadedToilets.get(toilet.getId());
                if (currentToilet != null) {
                    currentToilet.updateData(toilet);
                    mToiletRenderer.getMarker(currentToilet).hideInfoWindow();
                    mClusterManager.cluster();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openToiletSheet(Toilet toilet) {
        Intent intent = new Intent(getContext(), ToiletSheetActivity.class);
        intent.putExtra("toilet", toilet);
        startActivityForResult(intent, REQUEST_TOILET_SHEET);
    }

    @Override
    public boolean onClusterItemClick(Toilet toilet) {
        if (lastMarkerClicked == toilet) {
            lastMarkerClicked = null;
            openToiletSheet(toilet);
            return true;
        } else {
            lastMarkerClicked = toilet;
            return false;
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(Toilet toilet) {
        lastMarkerClicked = null;
        openToiletSheet(toilet);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mAddToiletEnabled) {
            float currentZoom = mMap.getCameraPosition().zoom;

            if (currentZoom < MIN_ZOOM_TO_ADD_TOILET) {
                ZoomBeforeAddToiletDialog zoomMax = new ZoomBeforeAddToiletDialog();
                zoomMax.show(getFragmentManager(), "zoomMax");
            } else {
                Toilet toilet = new Toilet();
                toilet.setPosition(latLng);

                AddToiletDialog addToiletDialog = new AddToiletDialog();
                Bundle args = new Bundle();
                args.putParcelable("toilet", toilet);
                addToiletDialog.setArguments(args);
                addToiletDialog.setTargetFragment(ToiletMapFragment.this, REQUEST_ADD_TOILET);

                addToiletDialog.show(getFragmentManager(), "adding toilets");
            }
        }
    }

    public boolean isToiletAddEnabled() {
        return mAddToiletEnabled;
    }

    public void onToiletCreationRequested() {
        if (!mToiletCreationReady) {
            mToiletCreationRequested = true;
            return;
        }

        if (!mMapReady) return;

        AddWithLongPressDialog canAddToiletDialog = new AddWithLongPressDialog();
        canAddToiletDialog.show(getFragmentManager(),"canAddToilet");

        mAddToiletEnabled = true;
        getActivity().supportInvalidateOptionsMenu();
    }

    public void onToiletCreationCanceled() {
        mAddToiletEnabled = false;
        getActivity().supportInvalidateOptionsMenu();
    }

    private void initAccessibilityLayout() {
        RelativeLayout accessibilityLayout = (RelativeLayout) getActivity().findViewById(R.id.accessibility_layout);
        ImageView upButton = (ImageView) accessibilityLayout.findViewById(R.id.up);
        ImageView downButton = (ImageView) accessibilityLayout.findViewById(R.id.down);
        ImageView leftButton = (ImageView) accessibilityLayout.findViewById(R.id.left);
        ImageView rightButton = (ImageView) accessibilityLayout.findViewById(R.id.right);
        ImageView zoomInButton = (ImageView) accessibilityLayout.findViewById(R.id.zoom_in);
        ImageView zoomOutButton = (ImageView) accessibilityLayout.findViewById(R.id.zoom_out);

        accessibilityLayout.setVisibility(View.VISIBLE);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double northsouth = Math.abs(latLngBounds.northeast.latitude - latLngBounds.southwest.latitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude + (northsouth / 5),
                            currentLatLng.longitude);

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double northsouth = Math.abs(latLngBounds.northeast.latitude - latLngBounds.southwest.latitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude - (northsouth / 5),
                            currentLatLng.longitude);

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double westeast = Math.abs(latLngBounds.southwest.longitude - latLngBounds.northeast.longitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude,
                            currentLatLng.longitude - (westeast / 5));

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double westeast = Math.abs(latLngBounds.southwest.longitude - latLngBounds.northeast.longitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude,
                            currentLatLng.longitude + (westeast / 5));

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    float targetZoom = Math.min(mMap.getMaxZoomLevel(), currentZoom + 1);

                    CameraPosition target = CameraPosition.builder().target(currentLatLng).zoom(targetZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    float targetZoom = Math.max(mMap.getMinZoomLevel(), currentZoom - 1);

                    CameraPosition target = CameraPosition.builder().target(currentLatLng).zoom(targetZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });
    }

    public void onDataChanged(List<Toilet> toiletList) {
        if (toiletList.isEmpty()) return;

        for (final Toilet t : toiletList) {
            Toilet downloadedToilet = mDownloadedToilets.get(t.getId());
            if (downloadedToilet == null) {
                mDownloadedToilets.put(t.getId(), t);
                if (mMapReady && mClusterManager != null) {
                    mClusterManager.addItem(t);
                }
            }
            else {
                downloadedToilet.updateData(t);
            }
        }

        // Update view
        if (mMapReady && mClusterManager != null) {
            mClusterManager.cluster();
        }
    }

    private void saveCameraPosition() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (mMapReady) {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putFloat("saved_latitude", (float)cameraPosition.target.latitude);
            editor.putFloat("saved_longitude", (float)cameraPosition.target.longitude);
            editor.putFloat("saved_zoom", cameraPosition.zoom);
            editor.putFloat("saved_bearing", cameraPosition.bearing);
            editor.apply();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (mClusterManager != null) {
            mClusterManager.onCameraChange(cameraPosition);
        }

        if (cameraPosition.zoom >= MIN_ZOOM_TO_SHOW_TOILET)
        {
            LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            LatLng northEast = latLngBounds.northeast;
            LatLng southWest = latLngBounds.southwest;

            if (isDataUpdateRequired(northEast, southWest))
            {
                Log.i(LOG_TAG, "Data Update Required");

                double latitudeDelta = northEast.latitude - southWest.latitude;
                double longitudeDelta = northEast.longitude - southWest.longitude;
                // 180th meridian management
                if (longitudeDelta < 0) {
                    longitudeDelta += 360;
                }

                mExtNorthEast = new LatLng(
                        northEast.latitude + latitudeDelta,
                        northEast.longitude + longitudeDelta);

                mExtSouthWest = new LatLng(
                        southWest.latitude - latitudeDelta,
                        southWest.longitude - longitudeDelta);

                LatLng topLeft = new LatLng(mExtNorthEast.latitude, mExtSouthWest.longitude);
                LatLng bottomRight = new LatLng(mExtSouthWest.latitude, mExtNorthEast.longitude);
                new ToiletDownloader(getContext()).requestMapToilets(topLeft, bottomRight,
                        new Downloader.Listener<List<Toilet>>() {
                            @Override
                            public void onResponse(List<Toilet> response) {
                                onDataChanged(response);
                            }
                        });
            }
        }
    }

    private boolean isLngOutOfBox(double lngExtWest, double lngExtEast, double lng) {
        // 180th meridian management
        if (lngExtWest > lngExtEast) {
            return !isLngOutOfBox(lngExtEast, lngExtWest, lng);
        } else {
            return lng < lngExtWest || lng > lngExtEast;
        }
    }

    private boolean isDataUpdateRequired(LatLng northEast, LatLng southWest) {
        if (mExtNorthEast == null || mExtSouthWest == null) return true;

        boolean outNorth = northEast.latitude > mExtNorthEast.latitude;
        boolean outSouth = southWest.latitude < mExtSouthWest.latitude;

        boolean outEast = isLngOutOfBox(mExtSouthWest.longitude, mExtNorthEast.longitude, northEast.longitude);
        boolean outWest = isLngOutOfBox(mExtSouthWest.longitude, mExtNorthEast.longitude, southWest.longitude);

        return outNorth || outSouth || outWest || outEast;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        lastMarkerClicked = null;
    }

    private class ToiletRenderer extends DefaultClusterRenderer<Toilet> {
        private final IconGenerator mClusterIconGenerator;
        private final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);

        public ToiletRenderer(Context context, GoogleMap map, ClusterManager<Toilet> clusterManager) {
            super(context, map, clusterManager);

            mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
            View clusterView = getActivity().getLayoutInflater().inflate(R.layout.map_cluster, null);
            mClusterIconGenerator.setContentView(clusterView);
            mClusterIconGenerator.setBackground(TRANSPARENT_DRAWABLE);
        }


        @Override
        protected void onBeforeClusterItemRendered(Toilet toilet, MarkerOptions markerOptions) {
            // Draw a single toilet.
            // Set the info window to show their name.

            if (toilet.isAdapted()) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pmr_pin));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.not_pmr_pin));
            }

            markerOptions.snippet(String.valueOf(toilet.getId()));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Toilet> cluster, MarkerOptions markerOptions) {
            // Draw cluster of toilets.
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            markerOptions.snippet("cluster");
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private class ToiletInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View view;
        private final TextView titleView;
        private final ImageView rankView;
        private final ImageView chargedView;

        public ToiletInfoWindowAdapter() {
            view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow, null);
            titleView = (TextView) view.findViewById(R.id.title);
            rankView = (ImageView) view.findViewById(R.id.rank);
            chargedView = (ImageView) view.findViewById(R.id.charged);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // Do nothing if a cluster is touched
            if (marker.getSnippet().equals("cluster"))
                return null;

            Integer toiletId = Integer.valueOf(marker.getSnippet());
            if (mDownloadedToilets.containsKey(toiletId)) {
                Toilet toilet = mDownloadedToilets.get(toiletId);
                titleView.setText(toilet.getName());
                rankView.setImageResource(Converters.resourceFromRank(toilet.getRankAverage(), toilet.getRateWeight()));
                chargedView.setImageResource(Converters.chargedFromBoolean(toilet.isCharged()));
            } else {
                titleView.setText("Error");
            }

            return view;
        }
    }
}

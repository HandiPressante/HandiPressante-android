package fr.handipressante.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;


public class MapFragment extends Fragment implements LocationListener, MapEventsReceiver {
    private final static int ZOOM = 14;
    private final static int DATA_UPDATE_TIME = 2000;
    private static IGeoPoint LAST_MAP_CENTER;

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private IMapController mMapController;
    private MyLocationNewOverlay mLocationOverlay;
    private RadiusMarkerClusterer mPoiMarkers;

    private LocationManager mLocationManager;

    private GeoPoint mTopLeft;
    private GeoPoint mBottomRight;
    private Timer mDataUpdateTimer;
    private TimerTask mDataUpdateTask;
    private Handler mDataUpdateHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("MapFragment", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("MapFragment", "onCreateView");

        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), mResourceProxy);

        mMapController = mMapView.getController();

        // activates the + and - (zoom)
        mMapView.setBuiltInZoomControls(true);

        // activates the multitouch control
        mMapView.setMultiTouchControls(true);

        // changes the map's style
        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setTilesScaledToDpi(true);

        // choose the zoom lvl
        mMapController.setZoom(ZOOM);

        if (LAST_MAP_CENTER == null) {
            // TODO : Load last known location
            LAST_MAP_CENTER = new GeoPoint(48.11005, -1.67930);
        }

        mLocationOverlay = new MyLocationNewOverlay(getContext(), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(false);
        mMapView.getOverlays().add(mLocationOverlay);

        //new overlay to listen when you click on the map
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mMapView.getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
        InfoWindow.closeAllInfoWindowsOn(mMapView);

        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("MapFragment", "onActivityCreated");

        // Get the location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null)
                    Toast.makeText(getContext(), "Provider network has a last known location.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Provider gps has a last known location.",
                        Toast.LENGTH_SHORT).show();
            }

            mLocationOverlay.setEnabled(false); // Default
            if (location != null) {
                onLocationChanged(location);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapController.setCenter(LAST_MAP_CENTER);

        //DataModel.instance().addMapToiletListener(this);
        //requestDataUpdate();
        startDataUpdateTimer();

        try {
            boolean isOneProviderEnabled = startLocationUpdates();
            mLocationOverlay.setEnabled(isOneProviderEnabled);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void startDataUpdateTimer() {
        mDataUpdateTimer = new Timer();
        initDataUpdateTask();
        mDataUpdateTimer.schedule(mDataUpdateTask, DATA_UPDATE_TIME, DATA_UPDATE_TIME);
    }

    private void stopDataUpdateTimer() {
        if (mDataUpdateTimer != null) {
            mDataUpdateTimer.cancel();
            mDataUpdateTimer = null;
        }
    }

    private void initDataUpdateTask() {
        mDataUpdateTask = new TimerTask() {
            @Override
            public void run() {
                mDataUpdateHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mTopLeft == null || mBottomRight == null) {
                            requestDataUpdate();
                        } else {
                            // Get the bounds of the map viewed
                            BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
                            GeoPoint topLeft = new GeoPoint(BB.getLatNorthE6(), BB.getLonWestE6());
                            GeoPoint bottomRight = new GeoPoint(BB.getLatSouthE6(), BB.getLonEastE6());

                            if (mTopLeft.getLatitude() != topLeft.getLatitude() || mTopLeft.getLongitude() != topLeft.getLongitude()
                                    || mBottomRight.getLatitude() != bottomRight.getLatitude() || mBottomRight.getLongitude() != bottomRight.getLongitude()) {
                                requestDataUpdate();
                            }
                        }
                    }
                });
            }
        };
    }

    private void requestDataUpdate() {

        Toast.makeText(getContext(), "requestDataUpdate",
                Toast.LENGTH_SHORT).show();

        // Get the bounds of the map viewed
        BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
        mTopLeft = new GeoPoint(BB.getLatNorthE6(), BB.getLonWestE6());
        mBottomRight = new GeoPoint(BB.getLatSouthE6(), BB.getLonEastE6());

        //DataManager.instance(getActivity().getApplicationContext()).requestMapToilets(mTopLeft, mBottomRight);
        new ToiletDownloader(getContext()).requestMapToilets(mTopLeft, mBottomRight,
                new Downloader.Listener<List<Toilet>>() {
                    @Override
                    public void onResponse(List<Toilet> response) {
                        onDataChanged(response);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        LAST_MAP_CENTER = mMapView.getMapCenter();

        //DataModel.instance().removeMapToiletListener(this);

        stopDataUpdateTimer();

        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean startLocationUpdates() throws SecurityException {
        boolean result = false;
        for (final String provider : mLocationManager.getProviders(true)) {
            mLocationManager.requestLocationUpdates(provider, 2*1000, 1.0f, this);
            result = true;
        }
        return result;
    }

    public void onDataChanged(List<Toilet> toiletList) {
        //ArrayList<POI> poiList = new ArrayList<>();

        if (toiletList.isEmpty()) return;

        if (mPoiMarkers != null) mMapView.getOverlays().remove(mPoiMarkers);


        mPoiMarkers = new RadiusMarkerClusterer(getContext());
        mPoiMarkers.getTextPaint().setTextSize(70.0f);
        mMapView.getOverlays().add(mPoiMarkers);

        for (final Toilet t : toiletList) {
            final Marker poiMarker = createMarker(t);
            mPoiMarkers.add(poiMarker);

            // parse Uri with coordinates of the poi.
            final Uri mUri = Uri.parse("geo:" + t.getCoordinates().getLatitude() + "," + t.getCoordinates().getLongitude() + "?q=" + t.getCoordinates().getLatitude() + "," + t.getCoordinates().getLongitude());
            //Listener that opens Maps when you click on Itinerary button
            poiMarker.getInfoWindow().getView().findViewById(R.id.bubble_itinerary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri geoLocation = mUri;

                    //intent to start a new activity
                    Intent intent = new Intent(Intent.ACTION_VIEW, geoLocation);
                    intent.setData(geoLocation);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            poiMarker.getInfoWindow().getView().findViewById(R.id.bubble_moreinfo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ToiletSheetActivity.class);
                    intent.putExtra("toilet", t);
                    startActivity(intent);
                }
            });
        }

        Drawable clusterIconD = getResources().getDrawable(R.drawable.cluster_full_mini);
        //clusterIconD.
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        mPoiMarkers.setIcon(clusterIcon);


        mMapView.invalidate();
    }

    // create a new marker
    private Marker createMarker(final Toilet t) {
        Marker newMarker = new Marker(mMapView);
        newMarker.setInfoWindow(new CustomInfoWindow(mMapView));
        newMarker.setPosition(t.getCoordinates());
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        newMarker.setTitle(t.getAddress());
        newMarker.setSnippet("snippet");
        //newMarker.setSubDescription("300 m");
        newMarker.setImage(getResources().getDrawable(Converters.rankFromInteger(t.getRankAverage()))); //getResources().getDrawable(R.drawable.star_five)
        newMarker.setIcon(getResources().getDrawable(Converters.pmrPinFromBoolean(t.isAdapted())));
        return newMarker;
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(getContext(), "Location changed",
                Toast.LENGTH_SHORT).show();


        GeoPoint newLocation = new GeoPoint(location);
        if (!mLocationOverlay.isEnabled()){
            // we get the location for the first time
            mLocationOverlay.setEnabled(true);
            mMapController.setCenter(newLocation);
            mMapController.animateTo(newLocation);
        }

        /*
        Toast.makeText(getContext(), "New Location (" + location.getLatitude() + "," + location.getLongitude() + ", provider : " + location.getProvider() + ", accuracy : " + location.getAccuracy(),
                Toast.LENGTH_SHORT).show();
                */
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        Toast.makeText(getContext(), "Tap on ("+geoPoint.getLatitude()+","+geoPoint.getLongitude()+")", Toast.LENGTH_SHORT).show();
        AddToiletDialog addToiletDialog = new AddToiletDialog();
        addToiletDialog.show(getFragmentManager(), "adding toilets");
        return false;
    }
}

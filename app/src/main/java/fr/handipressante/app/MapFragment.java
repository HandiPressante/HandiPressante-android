package fr.handipressante.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
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
import fr.handipressante.app.ToiletEdition.AddToiletDialog;


public class MapFragment extends Fragment implements LocationListener, MapEventsReceiver {
    private final static int ZOOM = 14;
    private final static int DATA_UPDATE_TIME = 700;
    private final static int MIN_ZOOM_SHOW = 10;

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private GeoPoint mMapCenter;
    private int mMapZoom;
    private IMapController mMapController;
    private MyLocationNewOverlay mLocationOverlay;
    private RadiusMarkerClusterer mPoiMarkers;
    private Bitmap mClusterIcon;
    private Drawable mMarkerIconAccessible;
    private Drawable mMarkerIconNotAccessible;

    private LocationManager mLocationManager;

    private GeoPoint mTopLeft;
    private GeoPoint mBottomRight;
    private GeoPoint mExtTopLeft;
    private GeoPoint mExtBottomRight;

    private Timer mDataUpdateTimer;
    private TimerTask mDataUpdateTask;
    private Handler mDataUpdateHandler = new Handler();

    SharedPreferences sharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("MapFragment", "onCreate");
        super.onCreate(savedInstanceState);

        mMarkerIconAccessible = ContextCompat.getDrawable(getActivity().getApplicationContext(), Converters.pmrPinFromBoolean(true));
        mMarkerIconNotAccessible = ContextCompat.getDrawable(getActivity().getApplicationContext(), Converters.pmrPinFromBoolean(false));

        BitmapDrawable clusterIcon = (BitmapDrawable) ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.cluster_full_mini);
        mClusterIcon = clusterIcon.getBitmap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("MapFragment", "onCreateView");

        mResourceProxy = new ResourceProxyImpl(getContext().getApplicationContext());

        //add layout with map and Arrows (by default)
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        mMapView = (MapView)rl.findViewById(R.id.map);

        mMapController = mMapView.getController();

        // changes the map's style
        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setTilesScaledToDpi(true);


        // choose the zoom lvl
        mMapController.setZoom(ZOOM);

        if (savedInstanceState != null) {
            mMapCenter = (GeoPoint) savedInstanceState.getParcelable("map_center");
            mMapZoom = savedInstanceState.getInt("map_zoom");
            Log.i("MapFragment", "Instance state loaded");
        }

        mLocationOverlay = new MyLocationNewOverlay(getContext(), mMapView);
        mLocationOverlay.enableMyLocation();
        // If activated : break the arrow controls ...
        // mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(false);
        mMapView.getOverlays().add(mLocationOverlay);

        //new overlay to listen when you click on the map
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mMapView.getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
        InfoWindow.closeAllInfoWindowsOn(mMapView);

        //removes the arrows according the SharedPreferences "slide"
        boolean slide = sharedPrefs.getBoolean("slide",false);

        //mMapView.setBuiltInZoomControls(slide);
        mMapView.setMultiTouchControls(slide);

        if(slide) {
            rl.removeView(rl.findViewById(R.id.maparrow));
        }
        else{
            useArrows(mMapController, rl);

            /*
            Problem : markers don't work
            // Disable dragging
            mMapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            */
        }

        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (mPoiMarkers != null) {
                    mPoiMarkers.setEnabled(event.getZoomLevel() >= MIN_ZOOM_SHOW);
                }
                return false;
            }
        });

        return rl;

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

                //if (location != null)
                    //Toast.makeText(getContext(), "Provider network has a last known location.",
                    //        Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getContext(), "Provider gps has a last known location.",
                //        Toast.LENGTH_SHORT).show();
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
        Log.i("MapFragment", "OnResume");
        super.onResume();

        if (mMapCenter == null)
            mMapCenter = new GeoPoint(48.11005, -1.67930);

        Log.i("MapFragment", "Set map center");
        mMapController.setCenter(mMapCenter);

        if (mMapZoom > 0) {
            mMapController.setZoom(mMapZoom);
        }

        //DataModel.instance().addMapToiletListener(this);
        requestDataUpdate();
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
                        // Get the bounds of the map viewed
                        BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
                        GeoPoint topLeft = new GeoPoint(BB.getLatNorthE6(), BB.getLonWestE6());
                        GeoPoint bottomRight = new GeoPoint(BB.getLatSouthE6(), BB.getLonEastE6());

                        if (topLeft.equals(mTopLeft) && bottomRight.equals(mBottomRight)) {
                            if (isDataUpdateRequired(topLeft, bottomRight)) {
                                requestDataUpdate();
                            }
                        } else {
                            mTopLeft = topLeft;
                            mBottomRight = bottomRight;
                        }
                    }
                });
            }
        };
    }

    private boolean isDataUpdateRequired(GeoPoint topLeft, GeoPoint bottomRight) {
        if (mExtTopLeft == null || mExtBottomRight == null) return true;

        boolean outNorth = topLeft.getLatitudeE6() > mExtTopLeft.getLatitudeE6();
        boolean outSouth = bottomRight.getLatitudeE6() < mExtBottomRight.getLatitudeE6();
        boolean outWest = topLeft.getLongitudeE6() < mExtTopLeft.getLongitudeE6();
        boolean outEast = bottomRight.getLongitudeE6() > mExtBottomRight.getLongitudeE6();
        return outNorth || outSouth || outWest || outEast;
    }

    private void requestDataUpdate() {
        if (mMapView.getZoomLevel() < MIN_ZOOM_SHOW) return;

        //Toast.makeText(getContext(), "requestDataUpdate",
        //        Toast.LENGTH_SHORT).show();

        BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
        GeoPoint topLeft = new GeoPoint(BB.getLatNorthE6(), BB.getLonWestE6());
        GeoPoint bottomRight = new GeoPoint(BB.getLatSouthE6(), BB.getLonEastE6());

        int latitudeDelta = topLeft.getLatitudeE6() - bottomRight.getLatitudeE6();
        int longitudeDelta = bottomRight.getLongitudeE6() - topLeft.getLongitudeE6();

        mExtTopLeft = new GeoPoint(BB.getLatNorthE6() + latitudeDelta, BB.getLonWestE6() - longitudeDelta);
        mExtBottomRight = new GeoPoint(BB.getLatSouthE6() - latitudeDelta, BB.getLonEastE6() + longitudeDelta);

        new ToiletDownloader(getContext()).requestMapToilets(mExtTopLeft, mExtBottomRight,
                new Downloader.Listener<List<Toilet>>() {
                    @Override
                    public void onResponse(List<Toilet> response) {
                        onDataChanged(response);
                    }
                });
    }

    @Override
    public void onPause() {
        Log.i("MapFragment", "OnPause");

        super.onPause();

        mMapCenter = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6());
        mMapZoom = mMapView.getZoomLevel();

        stopDataUpdateTimer();

        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        Log.i("MapFragment", "OnDestroy");
        super.onDestroy();

        if (mMapView != null && mPoiMarkers != null) {
            mMapView.getOverlays().remove(mPoiMarkers);
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);

        mMapCenter = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6());
        mMapZoom = mMapView.getZoomLevel();

        outState.putParcelable("map_center", mMapCenter);
        outState.putInt("map_zoom", mMapZoom);

        Log.i("MapFragment", "Instance state saved");
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

        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(getContext());

        poiMarkers.getTextPaint().setTextSize(70.0f);

        CustomInfoWindow infoWindow = new CustomInfoWindow(mMapView);

        for (final Toilet t : toiletList) {
            final Marker poiMarker = createMarker(t, infoWindow);
            poiMarkers.add(poiMarker);
            //give a reference to the toilet object (needed for CustomInfoWindow)
            poiMarker.setRelatedObject(t);

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
        }

        poiMarkers.setIcon(mClusterIcon);

        if (mPoiMarkers != null) {
            mMapView.getOverlays().remove(mPoiMarkers);
            //mMapView.invalidate();
        }

        mPoiMarkers = poiMarkers;
        mMapView.getOverlays().add(mPoiMarkers);
        mMapView.invalidate();
    }

    // create a new marker
    private Marker createMarker(final Toilet t, CustomInfoWindow infoWindow) {
        Marker newMarker = new Marker(mMapView);
        newMarker.setInfoWindow(infoWindow);
        newMarker.setPosition(t.getCoordinates());
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        newMarker.setTitle(t.getAddress());
        newMarker.setSnippet("snippet");
        //newMarker.setSubDescription("300 m");
        newMarker.setImage(getResources().getDrawable(Converters.rankFromInteger(t.getRankAverage()))); //getResources().getDrawable(R.drawable.star_five)
        if (t.isAdapted()) {
            newMarker.setIcon(mMarkerIconAccessible);
        } else {
            newMarker.setIcon(mMarkerIconNotAccessible);
        }

        return newMarker;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mMapCenter == null)
            mMapCenter = new GeoPoint(location);

        //Toast.makeText(getContext(), "Location changed",
        //        Toast.LENGTH_SHORT).show();
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(),
                R.drawable.my_location);


        GeoPoint newLocation = new GeoPoint(location);
        if (!mLocationOverlay.isEnabled()){
            // we get the location for the first time
            mLocationOverlay.setEnabled(true);
            mLocationOverlay.setPersonIcon(icon);

            /*if (mMapCenter == null) {
                Log.i("MapFragment", "Map center was null");
                mMapController.setCenter(newLocation);
                mMapController.animateTo(newLocation);
            }*/

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
        //Toast.makeText(getContext(), "Tap on ("+geoPoint.getLatitude()+","+geoPoint.getLongitude()+")", Toast.LENGTH_SHORT).show();

        Toilet toilet = new Toilet();
        toilet.setCoordinates(geoPoint);

        AddToiletDialog addToiletDialog = new AddToiletDialog();
        Bundle args = new Bundle();
        args.putParcelable("toilet", toilet);
        addToiletDialog.setArguments(args);

        addToiletDialog.show(getFragmentManager(), "adding toilets");
        return false;
    }

    public void useArrows(final IMapController mapController, RelativeLayout rl){
        ImageView buttonZoomPlus = (ImageView)rl.findViewById(R.id.zoom_plus);
        buttonZoomPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"click on zoom +", Toast.LENGTH_SHORT).show();
                mapController.zoomIn();
            }
        });

        ImageView buttonZoomLess = (ImageView)rl.findViewById(R.id.zoom_less);
        buttonZoomLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.zoomOut();
            }
        });

        ImageView buttonMoveDown = (ImageView)rl.findViewById(R.id.down);
        buttonMoveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mapController.scrollBy(0, 100);
            }
        });

        final ImageView buttonMoveUp = (ImageView)rl.findViewById(R.id.top);
        buttonMoveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.scrollBy(0, -200);
            }
        });


        ImageView buttonMoveRight = (ImageView)rl.findViewById(R.id.right);
        buttonMoveRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.scrollBy(200, 0);
            }
        });

        ImageView buttonMoveLeft = (ImageView)rl.findViewById(R.id.left);
        buttonMoveLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.scrollBy(-200, 0);
            }
        });
    }

    private void updateMarkersVisibility() {
        int zoom = mMapView.getZoomLevel();

        if (mPoiMarkers != null) {
            mPoiMarkers.setEnabled(zoom > 15);
        }
    }
}

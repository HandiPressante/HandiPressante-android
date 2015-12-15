package com.handipressante.handipressante;

import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;

import java.util.LinkedList;

import microsoft.mappoint.TileSystem;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IOverlayMenuProvider;
import org.osmdroid.views.overlay.Overlay.Snappable;
import org.osmdroid.views.util.constants.MapViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.Location;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;



import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.location.GpsStatus;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomButtonsController;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import java.util.List;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.kml.ColorStyle;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;
import java.util.ArrayList;
import java.util.LinkedList;

//creation of the Fragment
public class MapFragment extends Fragment {

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private List<Toilet> liste;
    private final static int ZOOM = 14;
    private Location loc;
    boolean gps = false;
    MyLocationNewOverlay mMyLocationOverlay;
    private IMapController map_controller;
<<<<<<< HEAD

=======
    Button button;
>>>>>>> origin/master
    public void setLoc(Location _loc){
        loc = _loc;
        //Log.e("yvo", "loc : " + loc);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        IMapController mapController = mMapView.getController();

        //activate the + and - (zoom)
        mMapView.setBuiltInZoomControls(true);

        //activate the multitouch control
        mMapView.setMultiTouchControls(true);
        //change the map's style

        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setTilesScaledToDpi(true);

        //choose the zoom lvl
        mMapView.setMaxZoomLevel(20);
        mapController.setZoom(ZOOM);

        MyLocation mloc = new MyLocation();
       // Log.e("yvo", " (mloc == null ?) : " + (mloc == null));
       // Log.e("yvo", "(mloc.locationResult == null ?): " + (mloc.locationResult == null));
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(50);
          //  Log.e("yvo", "sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      //  Log.e("yvo", "(loc2) : " + loc);

        if (loc != null) {
            gps = true;
        }
        final GeoPoint startPoint = gps_enabled();

        mapController.setCenter(startPoint);
        //mark creation
    /*    Marker startMarker = new Marker(mMapView);
        //use custom bubble info
        startMarker.setInfoWindow(new CustomInfoWindow(mMapView));
        //selection of the mark's coordinates
        startMarker.setPosition(startPoint);
        //display
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        //text which pop-up when you select the mark
        startMarker.setTitle("Pole Saint-Helier");
        startMarker.setSubDescription("Point de départ");
        //to change the icon
        startMarker.setIcon(getResources().getDrawable(R.drawable.yourmarker));
        //new end point
    */    //Marker newMarker = new Marker(mMapView);
        /*test implementation liste toilettes */
        
        /*fin test */
        mMyLocationOverlay = new MyLocationNewOverlay(getContext(), mMapView);
        final GeoPoint newPoint = new GeoPoint(48.112050, -1.677216,2944);
        Marker DestMarker = createMarker(newPoint);
        //mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(DestMarker);
        mMapView.invalidate();
        
        //new thread for navigation
        new Thread(new Runnable() {
            public void run() {

                RoadManager roadManager = new OSRMRoadManager();
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(startPoint);
                waypoints.add(newPoint);

                Road road = roadManager.getRoad(waypoints);
                try {
                    road = roadManager.getRoad(waypoints);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final Road finalRoad = road;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRoad.mStatus != Road.STATUS_OK) {
                            //handle error... warn the user, etc.
                        }

                        Polyline roadOverlay = RoadManager.buildRoadOverlay(finalRoad, Color.BLUE, 8, getContext());
                        mMapView.getOverlays().add(roadOverlay);
                    }
                });
            }
        }).start();

        
        map_controller = mapController;
        return mMapView;
    }

    //ouvrir sur google maps
    Uri geolocation;
    public Uri getUri() {
       Uri geoloc = geolocation;
        return geoloc;
    }

    //create a new marker
    public Marker createMarker(GeoPoint newPoint){
        Marker newMarker = new Marker(mMapView);
        newMarker.setInfoWindow(new CustomInfoWindow(mMapView));
        newMarker.setPosition(newPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        newMarker.setTitle("Parlement de Bretagne");
        newMarker.setSubDescription("300 m");
        newMarker.setImage(getResources().getDrawable(R.drawable.star_five));
        newMarker.setIcon(getResources().getDrawable(R.drawable.mymarker));
        return newMarker;
    }

    public GeoPoint gps_enabled(){
        if(gps){
            GeoPoint startPoint = new GeoPoint(loc);
            return startPoint;
        } else{
            //startpoint if gps not enabled (Pole Saint Helier- Rennes)
            GeoPoint startPoint = new GeoPoint(
            48.106681, -1.669463);
            return startPoint;
        }
    }
    public void onStart() {
        super.onStart();



        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                GeoPoint loc = mMyLocationOverlay.getMyLocation();
                if(loc != null){
                    mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
                }else{
                    loc = new GeoPoint(48.120624199999995, 1.6344577);
                }
            }
        });

        POI toilet = new POI(0);
        toilet.mCategory = "Toilet";
        toilet.mType = "Toilet";
        toilet.mLocation = new GeoPoint(loc);
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("http://nominatim.openstreetmap.org/");
        ArrayList<POI> poi_list = poiProvider.getPOICloseTo( new GeoPoint(loc), "Toilet", 50, 0.1);

        if (poi_list == null){
            poi_list = new ArrayList<>();
        }
        Log.e("handipressante", "toilet null ? " + (toilet == null));
        Log.e("handipressante", "poi_list null ? " + (poi_list == null));
        poi_list.add(toilet);

        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(mMapView.getContext());
        mMapView.getOverlays().add(poiMarkers);
        Drawable poiIcon = getResources().getDrawable(R.drawable.mymarker);
        for (POI poi:poi_list){
            Marker poiMarker = new Marker(mMapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            poiMarkers.add(poiMarker);
        }

        // Get the bounds of the map viewed
       /* BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
        GeoPoint South = new GeoPoint(BB.getLatSouthE6(), BB.getCenter().getLatitudeE6());
        GeoPoint North = new GeoPoint(BB.getLatNorthE6(), BB.getCenter().getLatitudeE6());
        GeoPoint East = new GeoPoint(BB.getCenter().getLongitudeE6(), BB.getLonEastE6());
        GeoPoint West = new GeoPoint(BB.getCenter().getLongitudeE6(), BB.getLonWestE6());*/
        mMyLocationOverlay.setEnabled(mMyLocationOverlay.isDrawAccuracyEnabled());
        //mMyLocationOverlay.setEnabled(mMyLocationOverlay.isOptionsMenuEnabled());
        //mMyLocationOverlay.setOptionsMenuEnabled(mMyLocationOverlay.isOptionsMenuEnabled());
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(mMyLocationOverlay.isDrawAccuracyEnabled());
        mMyLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(mMyLocationOverlay);
        // mMapView.getOverlays().add(createMarker(mMyLocationOverlay.getMyLocation()));

        mMapView.invalidate();

        //try to change the drawable of the actual position
/*
        Marker actualMarker = new Marker(mMapView);
        //text which pop-up when you select the mark
        actualMarker.setTitle("Position Actuelle");
        //to change the icon
        actualMarker.setIcon(getResources().getDrawable(R.drawable.yourmarker));
        int count =0;
        while(count<100){
            GeoPoint actual = mMyLocationOverlay.getMyLocation();
            Log.e("yvo", "isnull ? " + (actual == null));
            if(actual != null) {
                Log.e("yvo", actual.toString());
                //selection of the mark's coordinates
                actualMarker.setPosition(actual);
                actualMarker.setSubDescription(actual.toString());
                mMapView.getOverlays().add(actualMarker);
                mMapView.invalidate();
            }try {
                Thread.sleep(2000);
                Log.e("yvo", "sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
*/
<<<<<<< HEAD
=======







>>>>>>> origin/master
/*
        List<Overlay> mMyLocationOverlay = mMapView.getOverlays();

        GpsMyLocationProvider imlp = new GpsMyLocationProvider(MapFragment.this.getContext());
        imlp.setLocationUpdateMinDistance(100); // [m]  // Set the minimum distance for location updates
        imlp.setLocationUpdateMinTime(10000);   // [ms] // Set the minimum time interval for location updates
        mMyLocationOverlay = new MyLocationNewOverlay(MapFragment.this.getContext(), imlp, mMapView);
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        mMapView.getOverlays().add(mCellTowerGridMarkerClusterer);
        mMapView.getOverlays().add(mMyLocationOverlay);
        mMapView.getOverlays().add(mCompassOverlay);
        mMapView.getOverlays().add(mScaleBarOverlay);*/


        /*IMapController mapController = map_controller;
        MyLocation mloc = new MyLocation();
        Log.e("yvo", "2 (mloc == null ?) : " + (mloc == null));
        Log.e("yvo", "2(mloc.locationResult == null ?): " + (mloc.locationResult == null));
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(50);
            Log.e("yvo", "2sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("yvo", "2(loc2) : " + loc);

        if(loc!=null){
            gps = true;
        }
        GeoPoint actualPoint = new GeoPoint(loc);
        GeoPoint continuity = new GeoPoint(loc);
        //mark creation
        Marker myPosition = new Marker(mMapView);
        myPosition.setImage(getResources().getDrawable(R.drawable.yourmarker));
        mMapView.invalidate();
        int c = 0;

        while(c < 10000){
            if(gps){
                actualPoint = new GeoPoint(loc);
            }else{
                actualPoint = continuity;
            }
            map_controller.setCenter(actualPoint);
            //selection of the mark's coordinates
            myPosition.setPosition(actualPoint);
            continuity = actualPoint;
            map_controller = mapController;
            mMapView.getOverlays().add(myPosition);
            try {
                Thread.sleep(2500);
                Log.e("yvo", "2sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("yvo", "2(loc2) : " + loc);
            c++;
            mMapView.invalidate();
        }*/


    }
}


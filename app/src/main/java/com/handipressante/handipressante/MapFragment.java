package com.handipressante.handipressante;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.kml.ColorStyle;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.overlay.MinimapOverlay;
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
    private View map;
    private IMapController map_controller;
    public void setLoc(Location _loc){
        loc = _loc;
        Log.e("yvo", "loc : " + loc);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);

        //activate the + and - (zoom)
        mMapView.setBuiltInZoomControls(true);

        //activate the multitouch control
        mMapView.setMultiTouchControls(true);
        //change the map's style

        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setTilesScaledToDpi(true);

        IMapController mapController = mMapView.getController();
        //choose the zoom lvl
        mMapView.setMaxZoomLevel(20);
        mapController.setZoom(ZOOM);

        MyLocation mloc = new MyLocation();
        Log.e("yvo", " (mloc == null ?) : " + (mloc == null));
        Log.e("yvo", "(mloc.locationResult == null ?): " + (mloc.locationResult == null));
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(50);
            Log.e("yvo", "sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("yvo", "(loc2) : " + loc);


        if(loc!=null){
            gps = true;
        }
        final GeoPoint startPoint = gps_enabled();

        mapController.setCenter(startPoint);
        //mark creation
        Marker startMarker = new Marker(mMapView);
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
        //Marker newMarker = new Marker(mMapView);
        /*test implementation liste toilettes */


        /*fin test */
        final GeoPoint newPoint = new GeoPoint(48.112050, -1.677216,2944);
        Marker DestMarker = createMarker(newPoint);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(DestMarker);
        mMapView.invalidate();



        //new thread for navigation
        new Thread(new Runnable()
        {
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

                        Polyline roadOverlay = RoadManager.buildRoadOverlay(finalRoad, Color.RED, 8, getContext());
                        mMapView.getOverlays().add(roadOverlay);
                    }
                });
            }
        }).start();


        map = mMapView;
        map_controller = mapController;
        return mMapView;
    }

    public Marker createMarker(GeoPoint newPoint){
        Marker newMarker = new Marker(mMapView);

        newMarker.setInfoWindow(new CustomInfoWindow((MapView) mMapView));
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
 /*   public void onStart() {
        super.onStart();
        GeoPoint startPoint = new GeoPoint(loc);
        map.invalidate();
        while(true){
            startPoint = new GeoPoint(loc);
            map_controller.setCenter(startPoint);
        }
    }*/


}


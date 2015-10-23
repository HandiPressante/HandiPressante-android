package com.handipressante.handipressante;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.util.GeoPoint;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.support.v4.app.Fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.location.GpsStatus;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.StyleMap;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.ArrayList;
import java.util.LinkedList;

//creation of the Fragment
public class MapFragment extends Fragment {

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private final static int ZOOM = 14;
    private Location loc;
    public LocationManager locationManager;
    ArrayList<OverlayItem> anotherOverlayItemArray;
    //give the current position (comming from MyLocation) to loc
    public void setLoc(Location _loc){
        loc = _loc;
        Log.e("yvo", " (loc) : " + loc);
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

        //startpoint if gps not enabled (Rennes's townhall)
        GeoPoint startPoint = new GeoPoint(48.11137, -1.680145);
        if(loc!=null){
            startPoint = new GeoPoint(loc);
        }

        mapController.setCenter(startPoint);
        //mark creation
        Marker startMarker = new Marker(mMapView);
        //selection of the mark's coordinates
        startMarker.setPosition(startPoint);
        //display
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //text who pop-up when you select the mark
        startMarker.setTitle("Start point");
        //icone changing
        startMarker.setIcon(getResources().getDrawable(R.drawable.mymarker));
        Marker newMarker = new Marker(mMapView);
        GeoPoint newPoint = new GeoPoint(48.15,-1.07,2944);
        newMarker.setPosition(newPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Point test");
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(newMarker);


        return mMapView;
    }

}

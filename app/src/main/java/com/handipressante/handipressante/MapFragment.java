package com.handipressante.handipressante;

import org.osmdroid.tileprovider.tilesource.ITileSource;
        import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
        import org.osmdroid.views.overlay.MinimapOverlay;
        import org.osmdroid.views.overlay.ScaleBarOverlay;
        import org.osmdroid.views.overlay.compass.CompassOverlay;
        import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
        import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
        import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

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


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;
        import java.util.logging.Handler;

public class MapFragment extends Fragment {

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private final static int ZOOM = 14;
    //LocationManager locationManager = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
    //private MyLocation mloc = new MyLocation();
    //private MyLocation.LocationResult locResult;
    private Location loc;
    //MyLocation.LocationResult pos = mloc.getLocationResult();


    public void setLoc(Location _loc){
        loc = _loc;
        System.out.println("--------------------------------------------------------------- Testo (loc) : "+ loc);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);

        //Activer les boutons + et -
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        IMapController mapController = mMapView.getController();

        //Choisir le niveau de zoom
        mapController.setZoom(ZOOM);
        

        //choisir le point centre du depart
        //GeoPoint startPoint = new GeoPoint(48.120227199999995, -1.6345466);

        MyLocation mloc = new MyLocation();
        /*System.out.println("--------------------------------------------------------------- Testo (mloc == null ?) : " + (mloc == null));
        System.out.println("--------------------------------------------------------------- Testo (mloc.locationResult == null ?): " + (mloc.locationResult == null));*/
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(6000);
            System.out.println("-------------------- Testo : sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------------------- Testo (loc2) : " + loc);
        
        if (loc != null) {
            //lm = pos.getLocation();
            GeoPoint startPoint = new GeoPoint(loc);
            mapController.setCenter(startPoint);
        }

        return mMapView;
    }

}

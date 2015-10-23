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

//création de la vue Fragment
public class MapFragment extends Fragment {

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private final static int ZOOM = 14;
    //LocationManager locationManager = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
    //private MyLocation mloc = new MyLocation();
    //private MyLocation.LocationResult locResult;
    private Location loc;
    //MyLocation.LocationResult pos = mloc.getLocationResult();

    //initialiser startPoint
    //public Location mylocation;

    //Coordonnées par defaut
    public LocationManager locationManager;
    //Location location = null;
    ArrayList<OverlayItem> anotherOverlayItemArray;
    //donne la position courante(provenant de myLocation) à loc
    public void setLoc(Location _loc){
        loc = _loc;
     /*   System.out.println("--------------------------------------------------------------- Testo (loc) : "+ loc);*/
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
        //activer le controle multitouch
        mMapView.setMultiTouchControls(true);
        //Changer la couleur de la carte
        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        IMapController mapController = mMapView.getController();
        //Choisir le niveau de zoom
        mMapView.setMaxZoomLevel(20);
        mapController.setZoom(ZOOM);

        GpsMyLocationProvider imlp = new GpsMyLocationProvider(this.getContext());

        // public void setLocationUpdateMinDistance(final float meters) (distance de mise a jour)
        imlp.setLocationUpdateMinDistance(10);

        //choisir le point centre du depart
        //GeoPoint startPoint = new GeoPoint(48.120227199999995, -1.6345466);

        MyLocation mloc = new MyLocation();
        /*System.out.println("--------------------------------------------------------------- Testo (mloc == null ?) : " + (mloc == null));
        System.out.println("--------------------------------------------------------------- Testo (mloc.locationResult == null ?): " + (mloc.locationResult == null));*/
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(1000);
        /*    System.out.println("-------------------- Testo : sleep");*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       /* System.out.println("--------------------------------------------------------------- Testo (loc2) : " + loc);*/
        //lm = pos.getLocation();
        GeoPoint startPoint = new GeoPoint(loc);
        mapController.setCenter(startPoint);

        //créer un marqueur
        Marker startMarker = new Marker(mMapView);

        //choisir sa ses coordonees
        startMarker.setPosition(startPoint);
        //affichage
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //affichage en cliquant sur le point
        startMarker.setTitle("Start point");
        //changement de l'icône (normal que ce soit barré (à réécrire pour les versions supérieurs à l'API 22)
        startMarker.setIcon(getResources().getDrawable(R.drawable.mymarker));

        Marker newMarker = new Marker(mMapView);
        GeoPoint newPoint = new GeoPoint(48.15,-1.07,2944);
        newMarker.setPosition(newPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Point test");

        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(newMarker);


        //navigation
        //création de manager de navigation
        final RoadManager roadManager = new OSRMRoadManager();
        roadManager.addRequestOption("routeType=fastest");
        //ajout des points de depart et d'arrivee
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(48.15, -1.63);
        waypoints.add(endPoint);

        Marker end = new Marker(mMapView);
        end.setPosition(endPoint);
        end.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(end);

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, mMapView.getContext() );
        roadOverlay.setColor(Color.RED);
        mMapView.getOverlays().add(roadOverlay);

        Drawable nodeIcon = getResources().getDrawable(R.drawable.mymarker);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(mMapView);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step "+i);
            mMapView.getOverlays().add(nodeMarker);
        }
        //pour rafraichir la carte
        mMapView.invalidate();










        return mMapView;
    }

}

package com.handipressante.handipressante;


import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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



import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.StyleMap;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.LinkedList;

//création de la vue Fragment
public class MapFragment extends Fragment {

    private MapView mMapView;
    private MapController mMapController;
    private ResourceProxy mResourceProxy;
    private final static int ZOOM = 14;

    //initialiser startPoint
    public Location mylocation;

    //Coordonnées par defaut
    public LocationManager locationManager;
    Location location = null;
    ArrayList<OverlayItem> anotherOverlayItemArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);

        //mMapView.setMaxZoomLevel(30); //pour augmenter le zoom
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

        // public void setLocationUpdateMinDistance(final float meters) (distance de mise a jour
        imlp.setLocationUpdateMinDistance(10);

        //choisir le point centre du depart
        GeoPoint startPoint = new GeoPoint(48.1199094, -1.6345865, 2944);
        mapController.setCenter(startPoint);

        //créer un marqueur
        Marker startMarker = new Marker(mMapView);
        //choisir sa ses coordonees
        startMarker.setPosition(startPoint);
        //affichage
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //affichage en cliquant sur le point
        startMarker.setTitle("Start point");

        Marker newMarker = new Marker(mMapView);
        GeoPoint newPoint = new GeoPoint(50.000,-1.000,2944);
        newMarker.setPosition(newPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(newMarker);
        mMapView.invalidate();



        return mMapView;
    }

}


package com.handipressante.handipressante;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.NinePatchDrawable;
import android.media.Image;
import android.support.annotation.MainThread;
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
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
public class MapFragment extends Fragment implements MapEventsReceiver {
    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private List<Toilet> liste;
    private final static int ZOOM = 14;
    private Location loc;
    boolean gps = false;
    MyLocationNewOverlay mMyLocationOverlay;
    private IMapController map_controller;
    private TestDataModel testModel = new TestDataModel();
    private POI poi_dest;

    public void setLoc(Location _loc) {
        loc = _loc;
    }

    public void setDestination(GeoPoint geo) {
        poi_dest = new POI(0);
        poi_dest.mLocation = geo;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        //DO NOTHING FOR NOW:
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 512, mResourceProxy);
        IMapController mapController = mMapView.getController();

        //newoverlay to listen when you click on the map
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mMapView.getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
        InfoWindow.closeAllInfoWindowsOn(mMapView);

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
        mloc.locationResult.setMap(this);
        mloc.searchLocation(getContext(), mloc.locationResult);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
    */
        mMyLocationOverlay = new MyLocationNewOverlay(getActivity().getBaseContext(), mMapView);
        mMapView.invalidate();


        map_controller = mapController;
        return mMapView;
    }

    public void newRoad(final POI poi) {
        //new thread for navigation
        new Thread(new Runnable() {
            public void run() {

                final GeoPoint startPoint = gps_enabled();
                RoadManager roadManager = new OSRMRoadManager();
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(startPoint);
                waypoints.add(poi.mLocation);

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
    }

    //create a new marker
    public Marker createMarker(POI poi) {
        Marker newMarker = new Marker(mMapView);
        newMarker.setInfoWindow(new CustomInfoWindow(mMapView));
        newMarker.setPosition(poi.mLocation);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //newMarker.setTitle("Parlement de Bretagne");
        newMarker.setTitle(poi.mType);
        newMarker.setSnippet(poi.mDescription);
        //newMarker.setSubDescription("300 m");
        int i = R.drawable.star_zero;
        if (testModel.getToilet(poi.mLocation) != null) {
            i = testModel.getToilet(poi.mLocation).getRankIcon();
        }
        newMarker.setImage(getResources().getDrawable(i));//getResources().getDrawable(R.drawable.star_five)
        if (testModel.getToilet(poi.mLocation).isAdapted()) {
            newMarker.setIcon(getResources().getDrawable(R.drawable.pmr_pin));
        } else {
            newMarker.setIcon(getResources().getDrawable(R.drawable.not_pmr_pin));
        }
        return newMarker;
    }

    public Marker startMarker(GeoPoint geo)
    {
        Marker startM = new Marker(mMapView);
        startM.setIcon(getResources().getDrawable(R.drawable.map_start));
        startM.setPosition(geo);
        return startM;
    }

    public GeoPoint gps_enabled() {
        if (gps) {
            GeoPoint startPoint = new GeoPoint(loc);

            return startPoint;
        } else {
            //startpoint if gps not enabled (Pole Saint Helier- Rennes)
            GeoPoint startPoint = new GeoPoint(
                    48.106681, -1.669463);

            return startPoint;
        }

    }

    public void onStart() {
        super.onStart();
        MyLocation mloc = new MyLocation();
        mloc.locationResult.setMap(this);
        if (!gps) {
            new AlertDialog.Builder(mMapView.getContext())
                    .setTitle("Erreur")
                    .setMessage("Vous n'avez pas de GPS activé, merci de l'activer pour accéder à la carte")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = getActivity().getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            mMyLocationOverlay.runOnFirstFix(new Runnable() {
                public void run() {
                    GeoPoint loc = mMyLocationOverlay.getMyLocation();

                    if (loc != null) {
                        mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
                    } else {
                        loc = new GeoPoint(48.120624199999995, 1.6344577);
                    }

                }
            });
            // Get the bounds of the map viewed
            BoundingBoxE6 BB = mMapView.getProjection().getBoundingBox();
            Log.e("handipressante",BB.getCenter().toString());
            GeoPoint South = new GeoPoint(BB.getLatSouthE6(), BB.getCenter().getLongitudeE6());
            Log.e("handipressante",South.toString());
            GeoPoint North = new GeoPoint(BB.getLatNorthE6(), BB.getCenter().getLongitudeE6());
            GeoPoint East = new GeoPoint(BB.getCenter().getLatitudeE6(), BB.getLonEastE6());
            GeoPoint West = new GeoPoint(BB.getCenter().getLatitudeE6(), BB.getLonWestE6());
            //NominatimPOIProvider poiProvider = new NominatimPOIProvider("http://nominatim.openstreetmap.org/");
            ArrayList<POI> poi_list = new ArrayList<>();// = poiProvider.getPOICloseTo(new GeoPoint(loc), "Toilet", 50, 0.1);
            List<Toilet> listToilets = testModel.getToilets(West.getLongitude(), North.getLatitude(), East.getLongitude(), South.getLatitude());
            Log.e("handipressante", ""+ West.getLongitude()+" "+ North.getLatitude()+" "+ East.getLongitude()+" "+ South.getLatitude());


           /* if (poi_list == null) {
                poi_list = new ArrayList<>();
            }*/
            for (Toilet t : listToilets) {
                POI toilet = new POI(0);
                toilet.mCategory = "Toilet";
                toilet.mType = t.getAddress();
                toilet.mLocation = t.getGeo();
                poi_list.add(toilet);
            }

            if (poi_dest == null) {
                poi_dest = new POI(0);
                poi_dest.mLocation = new GeoPoint(48.1157242, -1.6443362);
            }

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(getActivity().getBaseContext());
            poiMarkers.getTextPaint().setTextSize(100.0f);
            mMapView.getOverlays().add(poiMarkers);
            for (final POI poi : poi_list) {
                final Marker poiMarker = createMarker(poi);

                poiMarkers.add(poiMarker);
                if (poi.mLocation.equals(poi_dest.mLocation)) {
                    poi_dest = poi;
                }
                //parse Uri with coordinates of the poi.
                final Uri mUri = Uri.parse("geo:" + poi.mLocation.getLatitude() + "," + poi.mLocation.getLongitude() + "?q=" + poi.mLocation.getLatitude() + "," + poi.mLocation.getLongitude());
                //Listener that opens Maps when tou click on Itinerary button
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

                        Integer idSheet = testModel.getToilet(poi.mLocation).getId();
                        if (idSheet != -1) // If it's not the default toilet
                        {
                            Intent intent = new Intent(getActivity(), ToiletSheetActivity.class);
                            Bundle b = new Bundle();
                            Log.d("Id send to the sheet", String.valueOf(idSheet));
                            b.putInt("idSheet", idSheet);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                });

            }
            Drawable clusterIconD = getResources().getDrawable(R.drawable.cluster_full);
            //clusterIconD.
            Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
            poiMarkers.setIcon(clusterIcon);

            mMyLocationOverlay.enableMyLocation();
            mMyLocationOverlay.setDrawAccuracyEnabled(false);
            mMyLocationOverlay.enableFollowLocation();
            mMapView.getOverlays().add(mMyLocationOverlay);
            mMapView.invalidate();

        }
    }
}


package com.handipressante.handipressante;

        import org.osmdroid.tileprovider.tilesource.ITileSource;
        import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
        import org.osmdroid.views.overlay.MinimapOverlay;
        import org.osmdroid.views.overlay.ScaleBarOverlay;
        import org.osmdroid.views.overlay.compass.CompassOverlay;
        import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
        import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
        import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

        import android.annotation.TargetApi;
        import android.content.Context;
        import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;


public class MapFragment extends Fragment {

    private ResourceProxy mResourceProxy;
    private MapView mMapView;
    private final static int ZOOM = 14;

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
        GeoPoint startPoint = new GeoPoint(48.1199094, -1.6345865, 2944);
        mapController.setCenter(startPoint);


        return mMapView;
    }

}

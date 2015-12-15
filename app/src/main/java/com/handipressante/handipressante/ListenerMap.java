package com.handipressante.handipressante;

/**
 * Created by marc on 21/10/2015.
 */
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.util.GeoPoint;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
public class ListenerMap implements LocationListener {

    private MapFragment mMapActivity;


    public ListenerMap(MapFragment aMapActivity)
    {
        this.mMapActivity = aMapActivity;
    }
    @Override
    public void onLocationChanged(Location location) {
        int latitude = (int) (location.getLatitude() * 1E6);
        int longitude = (int) (location.getLongitude() * 1E6);
        GeoPoint point = new GeoPoint(latitude, longitude);
       // mMapActivity.updateCarPosition(point);
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


}

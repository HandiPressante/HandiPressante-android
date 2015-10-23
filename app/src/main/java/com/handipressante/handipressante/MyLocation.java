package com.handipressante.handipressante;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

/**
 * Created by Yvonnig on 21/10/2015.
 */
public class MyLocation {
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    //Location _loc;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    Context c;

   /* public Location getLocation(){
        return _loc;
    }*/
    public MyLocation(){
        timer1 = new Timer();
        lm = null;
        locationResult = new LocationResult();
    }
    public boolean searchLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if(context == null){
         /*   System.out.println("------------------------------------------------- Testo : context null");*/
            return false;
        }

        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Exceptions will be thrown if the provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex) {
        }

        // Don't start listeners if no provider is enabled.
        if (!gps_enabled && !network_enabled) {
        /*    System.out.println("------------------------------------------------- Testo : gps non activé");*/
            return false;
        }

        if (gps_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListenerGps);
        if (network_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer1 = new Timer();
       /* System.out.println("------------------------------------------------- Testo : Recherche de la loc");*/
        c = context;
        timer1.schedule(new GetLastLocation(), 500);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            if(ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerNetwork);
                }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            /*System.out.println(MyApplication.getAppContext() + Manifest.permission.ACCESS_FINE_LOCATION);*/
            if(ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
         /*   System.out.println("------------------------------------------------- Testo : lancement de la recherche");*/
            if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);
            }
            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // If there are both values, use the latest one.
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
            /*    System.out.println("------------------------------------------------- Testo : gps & réseau dispo");*/
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
            /*    System.out.println("------------------------------------------------- Testo : gps dispo");*/
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc);
             /*   System.out.println("------------------------------------------------- Testo : réseau dispo");*/
                return;
            }
            System.out.println("------------------------------------------------- Testo : pas de loc trouvée");
            locationResult.gotLocation(null);
        }
    }

    public static class LocationResult {
        MapFragment mF;

        public void setMap(MapFragment m){
            mF = m;
        }

        public void gotLocation(Location location){
        /*    System.out.println("------------------------------------------------- Testo : gotLocation");*/
            mF.setLoc(location);
        }
       // public Location getLocation{return _loc;}
    }
}









/*
class MyLocation {
    Timer timer1;
    LocationManager lm;
    //LocationResult locationResult;
    Location loc;
    boolean gps_enabled = false;
    boolean network_enabled = false;

   /* public LocationResult getLocationResult(){
        return locationResult;
    }

    public Location getLocation(){
        return loc;
    }
/*
    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

        // Exceptions will be thrown if the provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // Don't start listeners if no provider is enabled.
        if (!gps_enabled && !network_enabled)
            return false;

        if (gps_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
           lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListenerGps);
        if (network_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 5000);
        return true;
    }

    public Location getLocation(Context context, Location result) {
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        loc = result;
        if (lm == null)
            lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

        // Exceptions will be thrown if the provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // Don't start listeners if no provider is enabled.
        if (!gps_enabled && !network_enabled)
            return null;

        if (gps_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListenerGps);
        if (network_enabled && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 5000);
        return loc;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            loc=location;
            if(ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            loc=location;
            if(ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            if(ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);
            }


            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // If there are both values, use the latest one.
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                    loc=gps_loc;
                else
                    loc=net_loc;
                return;
            }

            if (gps_loc != null) {
                loc=gps_loc;
                return;
            }
            if (net_loc != null) {
                loc=net_loc;
                return;
            }
            loc=null;
        }

    }
/*
    public class LocationResult{
        Location loc;
        public Location getLocation(){
            return loc;
        }
        public void gotLocation(Location location){
            loc = location;
        }
    }
}*/



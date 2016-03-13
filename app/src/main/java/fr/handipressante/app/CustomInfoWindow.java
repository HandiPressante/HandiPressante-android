package fr.handipressante.app;

import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * Created by marc on 05/11/2015.
 */
public class CustomInfoWindow extends MarkerInfoWindow{
    public CustomInfoWindow(MapView mapView) {
        super(R.layout.bubble, mapView);
    }
}

package fr.handipressante.app;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

import fr.handipressante.app.Data.Toilet;

/**
 * Created by marc on 05/11/2015.
 */
public class CustomInfoWindow extends MarkerInfoWindow {

    Toilet t;
    public CustomInfoWindow(final MapView mapView) {
        super(R.layout.bubble, mapView);

        ImageButton btn = (ImageButton)mView.findViewById(R.id.bubble_fiche);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t !=null){
                Intent intent = new Intent(v.getContext(), ToiletSheetActivity.class);
                intent.putExtra("toilet", t);
                v.getContext().startActivity(intent);
                }
            }
        });
    }
    @Override public void onOpen(Object item){
        super.onOpen(item);
        Marker marker = (Marker)item;
        t = (Toilet)marker.getRelatedObject();
        //mView.findViewById(R.id.bubble_fiche).setVisibility(View.VISIBLE);
    }

}

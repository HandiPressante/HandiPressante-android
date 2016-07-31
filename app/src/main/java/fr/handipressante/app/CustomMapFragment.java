package fr.handipressante.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.osmdroid.util.GeoPoint;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.ToiletEdition.AddToiletDialog;
import fr.handipressante.app.ToiletEdition.AddWithLongPressDialog;
import fr.handipressante.app.ToiletEdition.ZoomBeforeAddToiletDialog;

public class CustomMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private final String LOG_TAG = "HandipressanteApp";

    private boolean mAccessibilityOptionEnabled = true;

    private GoogleMap mMap;
    private boolean mMapReady = false;

    private final float MIN_ZOOM_TO_ADD_TOILET = 19.0f;
    private boolean mAddToiletEnabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Activate help menu
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mAccessibilityOptionEnabled = sharedPrefs.getBoolean("scroll_help", false);

        initAddToiletButton();
        if (mAccessibilityOptionEnabled) {
            initAccessibilityLayout();
        }

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intentHelp = new Intent(getContext(), HelpSlideMap.class);
                startActivity(intentHelp);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG_TAG, "Map ready");
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        if (mAccessibilityOptionEnabled) {
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            // todo
        }

        mMapReady = true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mAddToiletEnabled) {
            float currentZoom = mMap.getCameraPosition().zoom;

            if (currentZoom < MIN_ZOOM_TO_ADD_TOILET) {
                ZoomBeforeAddToiletDialog zoomMax = new ZoomBeforeAddToiletDialog();
                zoomMax.show(getFragmentManager(), "zoomMax");
            } else {
                Toilet toilet = new Toilet();
                GeoPoint geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
                toilet.setCoordinates(geoPoint);

                AddToiletDialog addToiletDialog = new AddToiletDialog();
                Bundle args = new Bundle();
                args.putParcelable("toilet", toilet);
                addToiletDialog.setArguments(args);

                addToiletDialog.show(getFragmentManager(), "adding toilets");
            }
        }
    }

    private void initAddToiletButton() {
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMapReady) return;

                if (!mAddToiletEnabled) {
                    AddWithLongPressDialog canAddToiletDialog = new AddWithLongPressDialog();
                    canAddToiletDialog.show(getFragmentManager(),"canAddToilet");

                    mAddToiletEnabled = true;

                    fab.setImageResource(R.drawable.ic_action_cancel);
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, 211, 47, 47)));
                } else {
                    mAddToiletEnabled = false;
                    fab.setImageResource(R.drawable.ic_action_new);
                    //color in blue
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, 22, 79, 134)));
                }
            }
        });
    }

    private void initAccessibilityLayout() {
        RelativeLayout accessibilityLayout = (RelativeLayout) getActivity().findViewById(R.id.accessibility_layout);
        ImageView upButton = (ImageView) accessibilityLayout.findViewById(R.id.up);
        ImageView downButton = (ImageView) accessibilityLayout.findViewById(R.id.down);
        ImageView leftButton = (ImageView) accessibilityLayout.findViewById(R.id.left);
        ImageView rightButton = (ImageView) accessibilityLayout.findViewById(R.id.right);
        ImageView zoomInButton = (ImageView) accessibilityLayout.findViewById(R.id.zoom_in);
        ImageView zoomOutButton = (ImageView) accessibilityLayout.findViewById(R.id.zoom_out);

        accessibilityLayout.setVisibility(View.VISIBLE);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double northsouth = Math.abs(latLngBounds.northeast.latitude - latLngBounds.southwest.latitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude + (northsouth / 5),
                            currentLatLng.longitude);

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double northsouth = Math.abs(latLngBounds.northeast.latitude - latLngBounds.southwest.latitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude - (northsouth / 5),
                            currentLatLng.longitude);

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double westeast = Math.abs(latLngBounds.southwest.longitude - latLngBounds.northeast.longitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude,
                            currentLatLng.longitude - (westeast / 5));

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double westeast = Math.abs(latLngBounds.southwest.longitude - latLngBounds.northeast.longitude);

                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    LatLng targetLatLng = new LatLng(
                            currentLatLng.latitude,
                            currentLatLng.longitude + (westeast / 5));

                    CameraPosition target = CameraPosition.builder().target(targetLatLng).zoom(currentZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    float targetZoom = Math.min(mMap.getMaxZoomLevel(), currentZoom + 1);

                    CameraPosition target = CameraPosition.builder().target(currentLatLng).zoom(targetZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapReady) {
                    LatLng currentLatLng = mMap.getCameraPosition().target;
                    float currentZoom = mMap.getCameraPosition().zoom;

                    float targetZoom = Math.max(mMap.getMinZoomLevel(), currentZoom - 1);

                    CameraPosition target = CameraPosition.builder().target(currentLatLng).zoom(targetZoom).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 400, null);
                }
            }
        });
    }
}

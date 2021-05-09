package com.tony.samplemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tony.samplemaps.DirectionHelpers.FetchURL;
import com.tony.samplemaps.DirectionHelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;

    List<MarkerOptions> markerOptionsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDirection =findViewById(R.id.btngetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchURL(MainActivity.this)
                        .execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });
        place1 = new MarkerOptions().position(new LatLng(-1.03326, 37.06933)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(-0.42013, 36.94759)).title("Location 2");

        markerOptionsList.add(place1);
        markerOptionsList.add(place2);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(place1);
        mMap.addMarker(place2);

        showAllMarkers();

    }

    private void showAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions m : markerOptionsList){
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    private String getUrl(LatLng origin, LatLng destination, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        String mode = "mode=" + directionMode;

        String parameter = str_origin + "&" + str_dest + "&" + mode;

        String format = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
                + parameter + "&=AIzaSyDGEhWRZIEvJAvVWvsOGbE1dP_K9rJnk0w";

        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }
}
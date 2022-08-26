package com.sih.adminkeyblue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sih.adminkeyblue.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    SearchView searchView;
    Marker marker;
    SupportMapFragment mapFragment;
    Location livelocation;
    Boolean locationPermissionGranted;
    RadiusAnimation groundAnimation;
    FusedLocationProviderClient client;
    public LatLng coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchView = (SearchView) findViewById(R.id.sv_location);
        mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        client = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String loaction = searchView.getQuery().toString();
                List<Address>addressList = null;
                if(loaction !=null || !loaction.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(loaction,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    clearall();
                    coordinates = latLng;
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(loaction));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }

    private void clearall() {
        mMap.clear();
        coordinates=null;
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getlivelocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    44);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == 44) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        getlivelocation();
    }


    public void getlivelocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Toast.makeText(this, "no permissions", Toast.LENGTH_SHORT).show();
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    livelocation=location;
                    LatLng latLng = new LatLng(livelocation.getLatitude(),livelocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your current Location").icon(BitmapDescriptorFactory.defaultMarker(205))).setTag("curr");
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f));
                    GroundOverlayOptions groundOverlayOptions= new GroundOverlayOptions()
                            .image(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
                            .position( latLng,50);


                    GroundOverlay groundOverlay = mMap.addGroundOverlay(groundOverlayOptions);
                    // mMap.addGroundOverlay(groundOverlayOptions);
                    groundAnimation = new RadiusAnimation(groundOverlay);
                    groundAnimation.setRepeatCount(Animation.INFINITE);
                    groundAnimation.setRepeatMode(Animation.RESTART);
                    groundAnimation.setDuration(2000);

                    mapFragment.getView().startAnimation(groundAnimation);
                    //Toast.makeText(MapsActivity.this, ""+location.toString(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapsActivity.this, "location not got", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                clearall();
                coordinates=latLng;
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            }
        });






    }

    public void sendlatlong(View view){
        setResult(Activity.RESULT_OK,
                new Intent().putExtra("latitude", coordinates.latitude).putExtra("longitude", coordinates.longitude));
        finish();
      //  Toast.makeText(this, ""+coordinates.toString(), Toast.LENGTH_SHORT).show();
    }
}
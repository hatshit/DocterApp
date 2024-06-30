package com.tac.reportingDemo.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tac.reportingDemo.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    @BindView(R.id.locationTextView)
    TextView locationTextView;
    @BindView(R.id.selectAddress)
    TextView selectAddress;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    //    private TextView locationTextView;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Location currentUserLocation; // Global variable to store current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("Address", locationTextView.getText().toString());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        if (currentUserLocation==null){


                        // Update currentUserLocation with the new location
                        currentUserLocation = location; // <-- Add this line

                        String locationString = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                        locationTextView.setText(locationString);
                        getAddressFromLatLong(location.getLatitude(), location.getLongitude());
                        if (mMap != null) { // Check if map is ready
                            updateMapWithLocation(); // Now updateMapWithLocation has a valid location to work with
                        }}
                    } else {
                        locationTextView.setText("Location not available");
                        selectAddress.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void getAddressFromLatLong(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        Log.e("latitude", "latitude--" + latitude);

        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                locationTextView.setText(address + " " + city + " " + country);
                selectAddress.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            locationTextView.setText("Address not available");
            selectAddress.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void updateMapWithLocation() {
        if (mMap != null && currentUserLocation != null) {
            LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
           /* mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location")
                    .icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_green))));*/
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15)); // Zoom to the user's location
}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition location = mMap.getCameraPosition();
                String locationString = "Lat: " + location.target.latitude + ", Lng: " + location.target.longitude;
                locationTextView.setText(locationString);
                locationTextView.setText(locationString);
                getAddressFromLatLong(location.target.latitude, location.target.longitude);
            }
        });
        try {
            // Load a style from a raw resource containing the JSON style declaration.
//            boolean success = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_json));

            boolean success = googleMap.setMapStyle(null);

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

        if (currentUserLocation != null) { // Make sure this is called if the location is already fetched before map is ready
            updateMapWithLocation();
        }
    }
}

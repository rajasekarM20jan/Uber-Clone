package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetDestinationPage extends AppCompatActivity {
    SearchView searchView;

    LatLng loc,loc1;
    MarkerOptions opt,opt1;
    SupportMapFragment myMap;
    FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination_page);
        myMap= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInDestination);

        searchView=findViewById(R.id.searchViewDestination);

        client= LocationServices.getFusedLocationProviderClient(SetDestinationPage.this);



        if (ContextCompat.checkSelfPermission(SetDestinationPage.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        myMap.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                double lat=location.getLatitude();
                                double lon=location.getLongitude();
                                loc= new LatLng(lat,lon);
                                opt=new MarkerOptions().position(loc);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));
                                googleMap.addMarker(opt);
                            }
                        });
                    }
                }
            });
        }



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Geocoder geocoder=new Geocoder(SetDestinationPage.this, Locale.getDefault());
                try {
                    List<Address> addressList= geocoder.getFromLocationName(s,1);
                    double lat=addressList.get(0).getLatitude();
                    double lon=addressList.get(0).getLongitude();
                    loc1= new LatLng(lat,lon);
                    opt1=new MarkerOptions().position(loc1);
                    myMap.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc1,12));

                            googleMap.clear();
                            googleMap.addMarker(opt);
                            googleMap.addMarker(opt1);
                            PolylineOptions opt=new PolylineOptions().add(loc).add(loc1);
                            Polyline poly= googleMap.addPolyline(opt);

                            searchView.clearFocus();

                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });




    }
}

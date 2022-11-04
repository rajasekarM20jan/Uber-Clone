package com.example.uber_clone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class HomeFragment extends Fragment {

    SupportMapFragment myMap;
    View view;
    FusedLocationProviderClient client;
    LatLng loc;
    Button searchDest;
    MarkerOptions opt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        searchDest =view.findViewById(R.id.buttonToDestination);
        myMap=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);


        client = LocationServices.getFusedLocationProviderClient(view.getContext());

        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
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
                                googleMap.setPadding(10,10,10,10);
                            }
                        });
                    }
                }
            });
        }

        searchDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(view.getContext(),SetDestinationPage.class);
                i.putExtra("type","ride");
                startActivity(i);
            }
        });







        return view;


    }
}
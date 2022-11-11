package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RidePage extends AppCompatActivity {

    FirebaseFirestore rideData,driverData;
    MarkerOptions opt,opt1,driverOpt;
    LatLng loc1,loc2,driverLoc;
    String rideID;
    String Status,driverMobile;
    SupportMapFragment map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_page);
        Intent i=getIntent();
        map=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInRide);
        rideID=i.getStringExtra("rideID");
        rideData=FirebaseFirestore.getInstance();
        driverData=FirebaseFirestore.getInstance();
        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap locA = (HashMap) documentSnapshot.get("pickUp");
                HashMap locB= (HashMap) documentSnapshot.get("drop");
                System.out.println("My Location In Ride : "+locA+"\t"+locB+rideID);

                loc1=new LatLng(Double.parseDouble(locA.get("latitude").toString()),Double.parseDouble(locA.get("longitude").toString()));
                System.out.println("My Location in loc1 : "+loc1);
                loc2=new LatLng(Double.parseDouble(locB.get("latitude").toString()),Double.parseDouble(locB.get("longitude").toString()));
                System.out.println("My Location in loc2 : "+loc2);
                getStatus();

            }
        });
    }

    void getStatus(){
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Status=documentSnapshot.get("rideStatus").toString();
                        System.out.println("My Ride Status: "+Status);
                        retrieveStatus();
                    }
                });
            }
        },10000);
    }

    void retrieveStatus(){
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                opt=new MarkerOptions();
                opt.position(loc1);
                opt.title("Pick Up");
                opt1=new MarkerOptions();
                opt1.position(loc2);
                opt1.title("Drop");

                switch (Status){

                    case "0":{
                        googleMap.addMarker(opt);
                        googleMap.addMarker(opt1);
                        googleMap.addPolyline(new PolylineOptions().add(loc1).add(loc2));
                        LatLngBounds bounds=new LatLngBounds.Builder().include(loc1).include(loc2).build();
                        Point pt=new Point();
                        getWindowManager().getDefaultDisplay().getSize(pt);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                        getStatus();
                        break;
                    }
                    case "1":{
                        googleMap.addMarker(opt);
                        googleMap.addMarker(opt1);

                        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverMobile=documentSnapshot.get("driverNumber").toString();
                            }
                        });
                        driverData.collection("drivers").document(driverMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverLoc=new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString()),Double.parseDouble(documentSnapshot.get("longitude").toString()));
                            }
                        });
                        driverOpt=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                        driverOpt.position(driverLoc);
                        googleMap.addMarker(driverOpt);
                        googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(loc1));
                        LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(loc1).build();
                        Point pt=new Point();
                        getWindowManager().getDefaultDisplay().getSize(pt);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                        getStatus();
                        break;
                    }
                    case "2":{
                        googleMap.addMarker(opt);
                        googleMap.addMarker(opt1);

                        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverMobile=documentSnapshot.get("driverNumber").toString();
                            }
                        });
                        driverData.collection("drivers").document(driverMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverLoc=new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString()),Double.parseDouble(documentSnapshot.get("longitude").toString()));
                            }
                        });
                        driverOpt=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                        driverOpt.position(driverLoc);
                        googleMap.addMarker(driverOpt);
                        googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(loc2));
                        LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(loc2).build();
                        Point pt=new Point();
                        getWindowManager().getDefaultDisplay().getSize(pt);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                        getStatus();
                        break;
                    }

                }




            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RidePage.this,DashBoard.class);
        startActivity(intent);
    }
}
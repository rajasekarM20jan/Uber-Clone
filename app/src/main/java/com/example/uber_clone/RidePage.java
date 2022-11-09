package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RidePage extends AppCompatActivity {

    FirebaseFirestore rideData,driverData;
    MarkerOptions opt,opt1;
    LatLng loc1,loc2,driverLoc;
    String rideID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_page);
        Intent i=getIntent();
        rideID=i.getStringExtra("rideID");
        rideData=FirebaseFirestore.getInstance();
        driverData=FirebaseFirestore.getInstance();
        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap locA = (HashMap) documentSnapshot.get("pickUp");
                HashMap locB= (HashMap) documentSnapshot.get("drop");
                System.out.println("My Location In Ride : "+locA+"\t"+locB+rideID);
            }
        });
    }
}
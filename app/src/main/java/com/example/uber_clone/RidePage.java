package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.grpc.Channel;

public class RidePage extends AppCompatActivity {

    FirebaseFirestore rideData,driverData;
    MarkerOptions opt,opt1,driverOpt;
    LatLng loc1,loc2,driverLoc;
    String rideID;
    Button tryAgain,cancelRide;
    String Status,driverMobile;
    SupportMapFragment map;
    ProgressDialog p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_page);
        SharedPreferences ridePref=getSharedPreferences("MyRide",MODE_PRIVATE);
        rideID=ridePref.getString("rideID","noRides");
        map=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInRide);
        rideData=FirebaseFirestore.getInstance();
        driverData=FirebaseFirestore.getInstance();
        tryAgain=findViewById(R.id.tryAgain);
        cancelRide=findViewById(R.id.cancelRide);

        System.out.println("Ride ID123 is : "+rideID);

        p=new ProgressDialog(RidePage.this);
        p.setCancelable(false);
        p.setMessage("Please Wait while we load data for you...");
        p.show();




        if(rideID.equals("noRides")){

        }
        else {
            rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HashMap locA = (HashMap) documentSnapshot.get("pickUp");
                    HashMap locB = (HashMap) documentSnapshot.get("drop");
                    System.out.println("My Location In Ride : " + locA + "\t" + locB + rideID);

                    loc1 = new LatLng(Double.parseDouble(locA.get("latitude").toString()), Double.parseDouble(locA.get("longitude").toString()));
                    System.out.println("My Location in loc1 : " + loc1);
                    loc2 = new LatLng(Double.parseDouble(locB.get("latitude").toString()), Double.parseDouble(locB.get("longitude").toString()));
                    System.out.println("My Location in loc2 : " + loc2);
                    rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            getStatus();
                        }
                    });


                }
            });
        }
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

                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                    NotificationChannel nChannel=
                            new NotificationChannel("My Notification","Notification"
                                    ,NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager=getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(nChannel);
                }

                switch (Status){

                    case "0":{

                        googleMap.clear();
                        googleMap.addMarker(opt);
                        googleMap.addMarker(opt1);
                        googleMap.addPolyline(new PolylineOptions().add(loc1).add(loc2));
                        LatLngBounds bounds=new LatLngBounds.Builder().include(loc1).include(loc2).build();
                        Point pt=new Point();
                        getWindowManager().getDefaultDisplay().getSize(pt);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                        if(p.isShowing()){
                            p.dismiss();
                        }
                        getStatus();
                        break;
                    }
                    case "1":{
                        tryAgain.setVisibility(View.INVISIBLE);
                        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverMobile = documentSnapshot.get("driverNumber").toString();

                                driverData.collection("drivers").document(driverMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        driverLoc = new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString()), Double.parseDouble(documentSnapshot.get("longitude").toString()));

                                        googleMap.clear();
                                        googleMap.addMarker(opt);
                                        googleMap.addMarker(opt1);
                                        driverOpt=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                        driverOpt.position(driverLoc);
                                        googleMap.addMarker(driverOpt);
                                        googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(loc1));
                                        LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(loc1).build();
                                        Point pt=new Point();
                                        getWindowManager().getDefaultDisplay().getSize(pt);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));

                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        getStatus();

                                    }
                                });
                            }
                        });

                        break;
                    }
                    case "2":{
                        cancelRide.setVisibility(View.INVISIBLE);
                        tryAgain.setVisibility(View.INVISIBLE);
                        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverMobile = documentSnapshot.get("driverNumber").toString();

                                driverData.collection("drivers").document(driverMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        driverLoc = new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString()), Double.parseDouble(documentSnapshot.get("longitude").toString()));

                                        googleMap.clear();
                                        googleMap.addMarker(opt);
                                        googleMap.addMarker(opt1);
                                        driverOpt=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                        driverOpt.position(driverLoc);
                                        googleMap.addMarker(driverOpt);
                                        googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(loc1));
                                        LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(loc1).build();
                                        Point pt=new Point();
                                        getWindowManager().getDefaultDisplay().getSize(pt);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                                        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        NotificationCompat.Builder builder =
                                                new NotificationCompat.Builder(RidePage.this,"My Notification")
                                                        .setSmallIcon(R.drawable.carmarker)
                                                        .setSound(uri)
                                                        .setContentTitle("Knock Knock!")
                                                        .setAutoCancel(true)
                                                        .setContentText("We are waiting outside..");


                                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.notify(0,builder.build());

                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        getStatus();
                                    }
                                });
                            }
                        });
                        break;
                    }
                    case "3":{
                        tryAgain.setVisibility(View.INVISIBLE);
                        cancelRide.setVisibility(View.INVISIBLE);
                        rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverMobile = documentSnapshot.get("driverNumber").toString();

                                driverData.collection("drivers").document(driverMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        driverLoc = new LatLng(Double.parseDouble(documentSnapshot.get("latitude").toString()), Double.parseDouble(documentSnapshot.get("longitude").toString()));
                                        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        NotificationCompat.Builder builder =
                                                new NotificationCompat.Builder(RidePage.this,"My Notification")
                                                        .setSmallIcon(R.drawable.carmarker)
                                                        .setSound(uri)
                                                        .setContentTitle("Gotcha!")
                                                        .setAutoCancel(true)
                                                        .setContentText("Enjoy Your Ride @ every moment with Uber...");
                                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.notify(1,builder.build());


                                        googleMap.clear();
                                        googleMap.addMarker(opt);
                                        googleMap.addMarker(opt1);
                                        driverOpt=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                        driverOpt.position(driverLoc);
                                        googleMap.addMarker(driverOpt);
                                        googleMap.addPolyline(new PolylineOptions().add(driverLoc).add(loc2));
                                        LatLngBounds bounds=new LatLngBounds.Builder().include(driverLoc).include(loc2).build();
                                        Point pt=new Point();
                                        getWindowManager().getDefaultDisplay().getSize(pt);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));

                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        getStatus();

                                    }
                                });
                            }
                        });
                        break;
                    }
                    case "4":{
                        tryAgain.setVisibility(View.INVISIBLE);
                        cancelRide.setVisibility(View.INVISIBLE);
                        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(RidePage.this,"My Notification")
                                        .setSmallIcon(R.drawable.carmarker)
                                        .setSound(uri)
                                        .setContentTitle("Ride Completed  :)")
                                        .setAutoCancel(true)
                                        .setContentText("Have a Good Day... Don't forget to choose us for next ride..");
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1,builder.build());
                        if(p.isShowing()){
                            p.dismiss();
                        }

                        goToDashBoard();
                    }
                }
            }
        });

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date d=new Date();
                SimpleDateFormat r=new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat s=new SimpleDateFormat("hh:mm:ss");
                String time=s.format(d);
                String date=r.format(d);
                rideData.collection("rides").document(rideID).update("rideDate",date);
                rideData.collection("rides").document(rideID).update("rideTime",time);

                getStatus();
            }
        });

        cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rideData.collection("rides").document(rideID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String rideStatus=documentSnapshot.get("rideStatus").toString();
                        if(rideStatus.equals("0")||rideStatus.equals("1")){
                            rideData.collection("rides").document(rideID).update("rideStatus","-1");
                            goToDashBoard();
                        }else{
                            Toast.makeText(RidePage.this, "You cannot cancel your ride now..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
    void goToDashBoard(){
        Intent intent=new Intent(RidePage.this,DashBoard.class);
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        goToDashBoard();
    }
}
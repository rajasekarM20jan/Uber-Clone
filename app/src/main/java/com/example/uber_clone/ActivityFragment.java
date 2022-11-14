package com.example.uber_clone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapters.UpcomingAdapter;
import adapters.previousAdapter;
import model.PreviousRide;
import model.RideList;


public class ActivityFragment extends Fragment {

    FirebaseFirestore ridesList;
    SharedPreferences sp;
    String phone,City,Fare,rideID,City2,Fare2,rideID2;
    HashMap h,h2;
    ArrayList allRides;
    ArrayList<RideList> ride;
    ArrayList<PreviousRide> previousRide;
    List<Address> address;
    ListView upcomingRidesList,previousRidesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_activity, container, false);
        upcomingRidesList=view.findViewById(R.id.upcomingRidesList);
        previousRidesList=view.findViewById(R.id.previousRidesList);
        allRides=new ArrayList<>();
        sp=getActivity().getSharedPreferences("MyMobile", Context.MODE_PRIVATE);
        phone=sp.getString("mobile","numberNotFound");

        ride=new ArrayList<>();
        previousRide=new ArrayList<>();

        ridesList=FirebaseFirestore.getInstance();
        ridesList.collection("rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(int i=0;i<queryDocumentSnapshots.size();i++){
                    allRides.add(queryDocumentSnapshots.getDocuments().get(i).getData());
                }
                for(int j=0;j<allRides.size();j++) {

                    h = (HashMap) allRides.get(j);

                    if (h.get("rideStatus").equals("0")) {
                        if (h.get("riderNumber").equals(phone)) {
                            HashMap drop = (HashMap) h.get("drop");
                            LatLng dropLatLng = new LatLng(Double.parseDouble(drop.get("latitude").toString()), Double.parseDouble(drop.get("longitude").toString()));
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                address = geocoder
                                        .getFromLocation(dropLatLng.latitude, dropLatLng.longitude, 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            City = address.get(0).getLocality();
                            Fare = h.get("rideFare").toString();
                            getRideID(j, City, Fare);
                        }
                    }
                }
            }
        });



        ridesList.collection("rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(int i=0;i<queryDocumentSnapshots.size();i++){
                    allRides.add(queryDocumentSnapshots.getDocuments().get(i).getData());
                }
                for(int j=0;j<allRides.size();j++) {

                    h2 = (HashMap) allRides.get(j);

                    if (h2.get("rideStatus").equals("-1")||h2.get("rideStatus").equals("4")) {
                        if (h2.get("riderNumber").equals(phone)) {
                            HashMap drop = (HashMap) h2.get("drop");
                            LatLng dropLatLng = new LatLng(Double.parseDouble(drop.get("latitude").toString()), Double.parseDouble(drop.get("longitude").toString()));
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                address = geocoder
                                        .getFromLocation(dropLatLng.latitude, dropLatLng.longitude, 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            City2 = address.get(0).getLocality();
                            Fare2 = h2.get("rideFare").toString();
                           /* getPreviousRideID(j, City2, Fare2);*/
                        }
                    }
                }
            }
        });





        return view;
    }

    /*private void getPreviousRideID(int abc, String city, String fare) {
        ridesList.collection("rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList a=new ArrayList<>();

                a.add(queryDocumentSnapshots.getDocuments().get(abc));
                DocumentSnapshot doc = (DocumentSnapshot) a.get(0);
                rideID = doc.getId();
                System.out.println("My Ride ID" + rideID);
                previousRide.add(new PreviousRide(rideID, city, fare));

                for(int k=0;k<ride.size();k++){
                    System.out.println("My List : "+ride.get(k).getRideID()
                            +"\t"+ride.get(k).getCity()
                            +"\t"+ride.get(k).getFare());
                }
                previousAdapter adapter=new previousAdapter(getActivity(),R.layout.custom_list_previous,previousRide);
                previousRidesList.setAdapter(adapter);

            }
        });
    }*/


    private void getRideID(int abcd,String city,String fare) {
        ridesList.collection("rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList a=new ArrayList<>();

                a.add(queryDocumentSnapshots.getDocuments().get(abcd));
                DocumentSnapshot doc = (DocumentSnapshot) a.get(0);
                rideID = doc.getId();
                System.out.println("My Ride ID" + rideID);
                ride.add(new RideList(rideID, city, fare));


                for(int k=0;k<ride.size();k++){
                    System.out.println("My List : "+ride.get(k).getRideID()
                            +"\t"+ride.get(k).getCity()
                            +"\t"+ride.get(k).getFare());
                }


                UpcomingAdapter adapter=new UpcomingAdapter(getActivity(),R.layout.custom_list_upcoming,ride);
                upcomingRidesList.setAdapter(adapter);


                upcomingRidesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String ridePageID=ride.get(i).getRideID();
                        SharedPreferences ridePref=getActivity().getSharedPreferences("MyRide"
                                ,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=ridePref.edit();
                        editor.putString("rideID", ridePageID);
                        editor.commit();
                        Intent intent=new Intent(getActivity(),RidePage.class);
                        startActivity(intent);
                    }
                });

            }
        });

    }
}
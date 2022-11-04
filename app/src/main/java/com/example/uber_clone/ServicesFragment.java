package com.example.uber_clone;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ServicesFragment extends Fragment {
    Button ride,rental,parcel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_services, container, false);

        ride=view.findViewById(R.id.rideButton);
        rental=view.findViewById(R.id.rentalButton);
        parcel=view.findViewById(R.id.packageButton);


        ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDestination("ride");
            }
        });
        rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDestination("rental");
            }
        });
        parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDestination("parcel");
            }
        });




        return view;
    }
    void goToDestination(String type){
        Intent intent=new Intent(getContext(),SetDestinationPage.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}
package com.example.uber_clone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AccountFragment extends Fragment {

    TextView nameText;
    FirebaseFirestore userDetails;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);
        nameText=view.findViewById(R.id.nameText);

        SharedPreferences sp= getActivity().getSharedPreferences("MyMobile", Context.MODE_PRIVATE);
        String phone=sp.getString("mobile","numberNotFound");

        userDetails=FirebaseFirestore.getInstance();
        userDetails.collection("users").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("qwertyuiop : "+task.getResult());
                    DocumentSnapshot doc= task.getResult();

                    System.out.println("qwertyuiop : "+doc.get("Name"));
                    String name= (String) doc.get("Name");
                    nameText.setText(name);




                }
            }
        });

        System.out.println("qwertyuiop : "+userDetails.collection("users").document(phone).get());

        return view;
    }
}
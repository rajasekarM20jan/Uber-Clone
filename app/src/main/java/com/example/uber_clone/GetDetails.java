package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GetDetails extends AppCompatActivity {

    EditText nameEt, lastNameEt;
    Button submit;
    FirebaseFirestore userDetails;
    SharedPreferences sp;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);
        nameEt=findViewById(R.id.nameET);
        lastNameEt=findViewById(R.id.lastNameET);
        submit=findViewById(R.id.submitDetails);
        userDetails=FirebaseFirestore.getInstance();
        sp=getSharedPreferences("MyMobile",MODE_PRIVATE);
        mobile=sp.getString("mobile","no");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameEt.length()==0){

                }else if(lastNameEt.length()==0){

                }else{
                    System.out.println("qwerty"+nameEt.getText().toString()+"\t"+lastNameEt.getText().toString());
                    String name=nameEt.getText().toString()+" "+lastNameEt.getText().toString();
                    getData(name);
                }
            }
        });
    }
    void getData(String name){
        Map<String,Object> user=new HashMap();
        user.put("Name",name);
        user.put("mobile",mobile);

        userDetails.collection("users").document(mobile).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(GetDetails.this, "Data added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
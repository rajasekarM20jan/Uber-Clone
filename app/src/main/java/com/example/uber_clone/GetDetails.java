package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileReader;

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
                    //fireStore need to
                }
            }
        });
    }
}
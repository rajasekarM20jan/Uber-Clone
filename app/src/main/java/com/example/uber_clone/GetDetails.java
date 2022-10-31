package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;

import java.io.FileReader;

public class GetDetails extends AppCompatActivity {

    EditText nameEt;
    RadioGroup genderRg;
    RadioButton maleRb,femaleRb,notToSayRb;
    Button submit;
    String userGender;
    FirebaseAuth userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);
        nameEt=findViewById(R.id.nameET);
        genderRg=findViewById(R.id.genderRg);
        maleRb=findViewById(R.id.maleRb);
        femaleRb=findViewById(R.id.femaleRb);
        notToSayRb=findViewById(R.id.notToSayRb);
        submit=findViewById(R.id.submitDetails);

        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int gender) {
                switch (gender){
                    case R.id.maleRb:{
                        userGender="Male";
                        break;
                    }
                    case R.id.femaleRb:{
                        userGender="Female";
                        break;
                    }
                    case R.id.notToSayRb:{
                        userGender="Prefer Not To Disclose";
                        break;
                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameEt.length()==0){

                }else if(userGender==null){

                }else{
                    System.out.println("qwerty"+nameEt.getText().toString()+"\t"+userGender);

                }
            }
        });
    }
}
package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText phoneEt;
    Button generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneEt=findViewById(R.id.phoneET);
        generate=findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneEt.length()!=10){
                    phoneEt.setError("Invalid Number");
                }else{
                    String phone="+91"+phoneEt.getText().toString();
                    send(phone);
                }
            }
        });

    }
    void send(String phone){
        Intent i=new Intent(MainActivity.this,VerificationPage.class);
        i.putExtra("phone",phone);
        startActivity(i);
    }



}
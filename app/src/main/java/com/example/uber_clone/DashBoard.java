package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashBoard extends AppCompatActivity {
    Fragment frag=null;
    BottomNavigationView btmView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        btmView=findViewById(R.id.bottomNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.dashBoard,new HomeFragment()).commit();

        btmView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeButton:{
                        frag=new HomeFragment();
                        break;
                    }
                    case R.id.servicesButton:{
                        frag=new ServicesFragment();
                        break;
                    }
                    case R.id.activitiesButton:{
                        frag=new ActivityFragment();
                        break;
                    }
                    case R.id.accountButton:{
                        frag=new AccountFragment();
                        break;
                    }
                }
                try{
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashBoard,frag).commit();
                }catch (Exception e){
                    e.printStackTrace();
                }


                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(frag==new HomeFragment()){
            AlertDialog.Builder exit=new AlertDialog.Builder(DashBoard.this);
            exit.setMessage("Are you Sure about to exit the application");
            exit.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            exit.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });
            exit.show();
        }else{
            frag=new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.dashBoard,frag).commit();
        }
    }
}
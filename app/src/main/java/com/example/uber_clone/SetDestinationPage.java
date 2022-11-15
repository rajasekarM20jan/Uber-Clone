package com.example.uber_clone;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetDestinationPage extends AppCompatActivity {
    SearchView searchView,searchViewFrom;
    ArrayList<LatLng> points;
    LatLng loc,loc1;
    int previousRideId,rideId;
    float totalFare,totalFareXL;
    CardView intercity,xlIntercity;
    ConstraintLayout carDetails;
    int price,priceForXL;
    int i;
    SharedPreferences sp;
    TextView backInDest,priceForIntercity,priceForXLIntercity,backInCarDetails;
    MarkerOptions opt,opt1;
    SupportMapFragment myMap;
    String phone;
    ProgressDialog progressDialog;
    Location startPoint,endPoint;
    FusedLocationProviderClient client;
    FirebaseFirestore driverLocationFetcher,rideData;
    ArrayList drivers,locationOfDrivers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination_page);

        progressDialog=new ProgressDialog(SetDestinationPage.this);
        progressDialog.setMessage("Please wait while we fetch the navigation routes...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        myMap= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInDestination);
        backInDest=findViewById(R.id.backInDest);
        backInCarDetails=findViewById(R.id.backInCarDetails);
        priceForIntercity=findViewById(R.id.priceForIntercity);
        priceForXLIntercity=findViewById(R.id.priceForXLIntercity);
        intercity=findViewById(R.id.cardViewForIntercity);
        xlIntercity=findViewById(R.id.cardViewForXLIntercity);
        searchView=findViewById(R.id.searchViewDestination);
        carDetails=findViewById(R.id.carDetails);
        drivers=new ArrayList<>();
        sp=getSharedPreferences("MyMobile", Context.MODE_PRIVATE);
        phone=sp.getString("mobile","numberNotFound");
        locationOfDrivers=new ArrayList<>();
        rideData=FirebaseFirestore.getInstance();
        driverLocationFetcher=FirebaseFirestore.getInstance();
        searchViewFrom=findViewById(R.id.searchViewFrom);
        Intent intent=getIntent();
        String type=intent.getStringExtra("type");


        rideData.collection("rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("rides Size"+queryDocumentSnapshots.getDocuments().size()
                        +"\t"+queryDocumentSnapshots.size());
                i=queryDocumentSnapshots.getDocuments().size();
                System.out.println("rides Size"+i);
                previousRideId=100000+i;
                rideId=previousRideId+1;
            }
        });

        switch(type){
            case "ride":{
                price=25;
                priceForXL=40;
                break;
            }
            case "rental":{
                price=13;
                priceForXL=20;
                break;
            }
            case "parcel":{
                price=20;
                priceForXL=40;
                break;
            }
        }



        client= LocationServices.getFusedLocationProviderClient(SetDestinationPage.this);



        if (ContextCompat.checkSelfPermission(SetDestinationPage.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        myMap.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                double lat=location.getLatitude();
                                double lon=location.getLongitude();
                                loc= new LatLng(lat,lon);
                                opt=new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));
                                googleMap.addMarker(opt);
                                        for(int i=0;i<locationOfDrivers.size();i++) {
                                            try {
                                                ArrayList locations = (ArrayList) locationOfDrivers.get(i);
                                                String a = (String) locations.get(0);
                                                String b = (String) locations.get(1);
                                                double latitudeOfDriver = Double.parseDouble(a);
                                                double longitudeOfDriver = Double.parseDouble(b);
                                                LatLng latLngOfDriver = new LatLng(latitudeOfDriver, longitudeOfDriver);
                                                MarkerOptions myDriverOpt = new MarkerOptions().position(latLngOfDriver);
                                                myDriverOpt.title("Driver");
                                                googleMap.addMarker(myDriverOpt);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                searchViewFrom.setQuery(String.valueOf(opt),false);
                            }
                        });
                    }
                }
            });
        }


        backInDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchViewFrom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchViewFrom.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Geocoder geocoder=new Geocoder(SetDestinationPage.this, Locale.getDefault());
                try {
                    List<Address> addressList= geocoder.getFromLocationName(s,1);
                    double lat=addressList.get(0).getLatitude();
                    double lon=addressList.get(0).getLongitude();
                    loc= new LatLng(lat,lon);
                    opt=new MarkerOptions().position(loc);

                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });

        driverLocationFetcher.collection("drivers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    System.out.println("DriverData Size : " + queryDocumentSnapshots.size());
                    System.out.println("DriverData : " + queryDocumentSnapshots.getDocuments().get(i).getData());
                    drivers.add(queryDocumentSnapshots.getDocuments().get(i).getData());
                }
                for(int j=0;j<drivers.size();j++){
                    ArrayList d=new ArrayList<>();
                    System.out.println("DriverData 2 : "+drivers.get(j).toString());
                    HashMap hashMap= (HashMap) drivers.get(j);
                    System.out.println("DriverData hash : "+hashMap.get("loginStatus"));
                    if(hashMap.get("loginStatus").equals("Online")){
                        String lat1= (String) hashMap.get("latitude");
                        String lon1= (String) hashMap.get("longitude");
                        d.add(lat1);
                        d.add(lon1);
                        System.out.println("DriverData Location d : "+d);
                    }
                    locationOfDrivers.add(d);
                }
                System.out.println("DriverData Location : "+locationOfDrivers);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Geocoder geocoder=new Geocoder(SetDestinationPage.this, Locale.getDefault());
                try {
                    List<Address> addressList= geocoder.getFromLocationName(s,1);
                    double lat=addressList.get(0).getLatitude();
                    double lon=addressList.get(0).getLongitude();
                    loc1= new LatLng(lat,lon);
                    opt1=new MarkerOptions().position(loc1);
                    searchView.clearFocus();
                    direction();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    void direction(){

        String location1=loc1.latitude+","+ loc1.longitude;
        String location=loc.latitude+","+loc.longitude;
        System.out.println("MyLocations : "+location+"\t"+location1);
        String url= Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination",location1)
                .appendQueryParameter("origin",location)
                .appendQueryParameter("mode","driving")
                .appendQueryParameter("key","AIzaSyBD6tQfrRdFmABCJ9DIb6OXnZga_2OgX0A")
                .toString();
        OkHttpClient myClient=new OkHttpClient();
        Request fetchData= new Request.Builder().url(url).build();
        myClient.newCall(fetchData).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("My Response: Error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                    SetDestinationPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        /*try {
                            JSONObject arr= new JSONObject(resultData);


                            System.out.println("MyLocations5 : "+arr);
                            JSONArray routes=arr.getJSONArray("routes");

                            for(int i=0;i<routes.length();i++){

                                points=new ArrayList<>();


                                JSONArray legs= routes.getJSONObject(i).getJSONArray("legs");
                                System.out.println("MyLocations6 : ");

                                for(int j=0;j<legs.length();j++){

                                    JSONArray steps=legs.getJSONObject(j).getJSONArray("steps");
                                    System.out.println("MyLocations7 : ");

                                    for(int k=0;k<steps.length();k++){
                                        String polyLine= steps.getJSONObject(k).getJSONObject("polyline").getString("points");

                                        List<LatLng> list= decodePoly(polyLine);
                                        System.out.println("MyLocations8 : ");
                                        for(int l=0;l<list.size();l++){
                                            LatLng position=new LatLng((list.get(l).latitude),(list.get(l).longitude));
                                            points.add(position);

                                        }


                                    }
                                    //------------------------------------

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("My Locations3 :");
                        }*/

                        try{
                            myMap.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    System.out.println("Location Array Size : "+locationOfDrivers.size());




                                    googleMap.clear();

                                    googleMap.addMarker(opt).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));
                                    googleMap.addMarker(opt1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.squaremarker));

                                    for(int i=0;i<locationOfDrivers.size();i++){
                                        System.out.println("Location Array Size : "+locationOfDrivers.size());
                                        try {
                                            ArrayList locations = (ArrayList) locationOfDrivers.get(i);
                                            String a = (String) locations.get(0);
                                            String b = (String) locations.get(1);
                                            double latitudeOfDriver = Double.parseDouble(a);
                                            double longitudeOfDriver = Double.parseDouble(b);
                                            LatLng latLngOfDriver = new LatLng(latitudeOfDriver, longitudeOfDriver);
                                            MarkerOptions myDriverOpt = new MarkerOptions()
                                                    .position(latLngOfDriver)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                            myDriverOpt.title("Driver");
                                            googleMap.addMarker(myDriverOpt);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    /*int myCount= points.size();
                                    System.out.println("My Points Count : "+myCount);

                                    if(myCount<=2000){
                                        googleMap.addPolyline(new PolylineOptions().addAll(points));
                                    }
                                    else{
                                        googleMap.addPolyline(new PolylineOptions().add(loc).add(loc1));
                                    }*/

                                    googleMap.addPolyline(new PolylineOptions().add(loc).add(loc1));
                                    LatLngBounds bounds=new LatLngBounds.Builder().include(loc).include(loc1).build();
                                    Point pt=new Point();
                                    getWindowManager().getDefaultDisplay().getSize(pt);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,800,30));
                                    progressDialog.dismiss();


                                }
                            });

                        }catch (Exception e){
                            e.getMessage();
                        }
                    }

                });




                }
                else{
                    System.out.println("MyLocations2 : "+url);
                }
            }

        });

        startPoint=new Location("location A");
        startPoint.setLatitude(loc.latitude);
        startPoint.setLongitude(loc.longitude);

        endPoint=new Location("Location B");
        endPoint.setLongitude(loc1.longitude);
        endPoint.setLatitude(loc1.latitude);

        float distance= (int) startPoint.distanceTo(endPoint);

        totalFare= price*(distance/1000);

        totalFareXL=priceForXL*(distance/1000);

        System.out.println("My Locations distance and Price : "+ (distance/1000)+"\t Price :"+totalFare);
        System.out.println("My Locations distance and Price For XL : "+ (distance/1000)+"\t Price :"+totalFareXL);

        priceForIntercity.setText("₹"+(int)totalFare);
        priceForXLIntercity.setText("₹"+(int)totalFareXL);

        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                carDetails.setVisibility(View.VISIBLE);
                carDetails.setAnimation(AnimationUtils.loadAnimation(SetDestinationPage.this,R.anim.down));
            }
        },2000);



        intercity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date d=new Date();
                SimpleDateFormat sf_date= new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sf_time=new SimpleDateFormat("hh:mm:ss");
                String my_date=sf_date.format(d);
                String my_time=sf_time.format(d);
                Map<String,Object> ride=new HashMap();
                ride.put("rideFare",String.valueOf((int)totalFare));
                ride.put("rideDate",my_date);
                ride.put("rideTime",my_time);
                ride.put("pickUp",loc);
                ride.put("drop",loc1);
                ride.put("riderNumber",phone);
                /*
                    Ride Status:
                        0- Not Assigned
                        1- Assigned
                        2- Reached PickUp Location
                        3- Ride Started
                        4- Ride Complete
                       -1- Cancelled
                */
                ride.put("rideStatus","0");
                ride.put("carType","intercity");
                ride.put("driverAssigned","no");
                ride.put("driverName","");
                ride.put("driverNumber","");


                rideData.collection("rides").document(Integer.toString(rideId)).set(ride).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        SharedPreferences ridePref=getSharedPreferences("MyRide",MODE_PRIVATE);
                        SharedPreferences.Editor editor=ridePref.edit();
                        editor.putString("rideID", String.valueOf(rideId));
                        editor.commit();

                        Intent intent =new Intent(SetDestinationPage.this,RidePage.class);
                        startActivity(intent);
                    }
                });
            }
        });

        xlIntercity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date d=new Date();
                SimpleDateFormat sf_date= new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sf_time=new SimpleDateFormat("hh:mm:ss");
                String my_date=sf_date.format(d);
                String my_time=sf_time.format(d);
                System.out.println("MyDate : "+my_date+"\tmyTime : "+my_time);


                Map<String,Object> ride=new HashMap();
                ride.put("rideFare",String.valueOf((int)totalFareXL));
                ride.put("rideDate",my_date);
                ride.put("rideTime",my_time);
                ride.put("riderNumber",phone);
                ride.put("rideStatus","0");
                ride.put("pickUp",loc);
                ride.put("drop",loc1);
                ride.put("carType","xlIntercity");
                ride.put("driverAssigned","no");
                ride.put("driverName","");
                ride.put("driverNumber","");


                rideData.collection("rides").document(Integer.toString(rideId)).set(ride).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SharedPreferences ridePref=getSharedPreferences("MyRide",MODE_PRIVATE);
                        SharedPreferences.Editor editor=ridePref.edit();
                        editor.putString("rideID", String.valueOf(rideId));
                        editor.commit();

                        Intent intent =new Intent(SetDestinationPage.this,RidePage.class);

                        startActivity(intent);
                    }
                });

            }
        });


        backInCarDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carDetails.setAnimation(AnimationUtils.loadAnimation(SetDestinationPage.this,R.anim.gone));
                carDetails.setVisibility(View.GONE);
            }
        });
    }
    private List<LatLng> decodePoly(String encoded){
        System.out.println("MyLocations polyline : "+encoded);
        List<LatLng> poly=new ArrayList<>();
        int index=0, len=encoded.length(),latitude=0,longitude=0;
        while (index<len){
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |=(b & 0x1f)<<shift;
                shift+=5;
            }while (b>=0x20);
            int dlat=((result & 1)!=0? ~(result>>1):(result>>1));
            latitude += dlat;

            shift=0;
            result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |=(b & 0x1f)<<shift;
                shift+=5;
            }while (b>=0x20);
            int dlng=((result & 1)!=0? ~(result>>1):(result>>1));
            longitude += dlng;
            double a=(double) latitude/1E5;
            double z=(double) longitude/1E5;

            LatLng p= new LatLng(a,z);
            poly.add(p);

        }
        return poly;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

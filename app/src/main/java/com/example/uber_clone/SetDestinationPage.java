package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetDestinationPage extends AppCompatActivity {
    SearchView searchView;

    LatLng loc,loc1;
    MarkerOptions opt,opt1;
    SupportMapFragment myMap;
    PolylineOptions options=null;
    FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination_page);
        myMap= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapInDestination);

        searchView=findViewById(R.id.searchViewDestination);

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
                                opt=new MarkerOptions().position(loc);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));
                                googleMap.addMarker(opt);
                            }
                        });
                    }
                }
            });
        }



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
                    String resultData;
                    resultData=response.body().string();



                SetDestinationPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject arr= new JSONObject(resultData);


                            System.out.println("MyLocations5 : "+arr);
                            JSONArray routes=arr.getJSONArray("routes");

                            ArrayList<LatLng> points;


                            for(int i=0;i<routes.length();i++){
                                points=new ArrayList<>();
                                options=new PolylineOptions();

                                JSONArray legs= routes.getJSONObject(i).getJSONArray("legs");

                                System.out.println("MyLocations legs : "+legs);

                                for(int j=0;j<legs.length();j++){

                                    JSONArray steps=legs.getJSONObject(j).getJSONArray("steps");
                                    System.out.println("MyLocations steps : "+steps);

                                    for(int k=0;k<steps.length();k++){
                                        System.out.println("MyLocations poly-1 : "+steps.getJSONObject(k).getJSONObject("polyline"));
                                        String polyLine= steps.getJSONObject(k).getJSONObject("polyline").getString("points");

                                        List<LatLng> list= decodePoly(polyLine);
                                        System.out.println("MyLocations list : "+list);

                                        for(int l=0;l<list.size();l++){
                                            LatLng position=new LatLng((list.get(l).latitude),(list.get(l).longitude));
                                            points.add(position);
                                            System.out.println("MyLocations Position : "+points);
                                            System.out.println("MyLocations points : "+points);
                                        }
                                        options.addAll(points);
                                        options.width(10);
                                        options.color(R.color.black);
                                        options.geodesic(true);

                                    }
                                    myMap.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(@NonNull GoogleMap googleMap) {
                                            System.out.println("MyLocations points2 : "+options.getPoints());
                                            googleMap.clear();

                                            googleMap.addMarker(opt);
                                            googleMap.addMarker(opt1);

                                            googleMap.addPolyline(options);

                                            LatLngBounds bounds=new LatLngBounds.Builder().include(loc).include(loc1).build();
                                            Point pt=new Point();
                                            getWindowManager().getDefaultDisplay().getSize(pt);
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,pt.x,150,30));



                                        }
                                    });

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("My Locations3 :");
                        }
                    }

                });
                }
                else{
                    System.out.println("MyLocations2 : "+url);
                }
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
}

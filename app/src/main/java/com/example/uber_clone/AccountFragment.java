package com.example.uber_clone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class  AccountFragment extends Fragment {

    TextView nameText;
    FirebaseFirestore userDetails;
    ImageView profilePic;
    ActivityResultLauncher<Intent> dpGetter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        nameText=view.findViewById(R.id.nameText);
        profilePic=view.findViewById(R.id.profilePic);

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

                    String image=(String) doc.get("displayPicture");

                    if(image.equals("")){ }
                    else{
                        Uri imgUri=Uri.parse(image);
                        profilePic.setImageURI(imgUri);
                    }

                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dpGetter.launch(i);
                System.out.println("executed00");
            }
        });

        System.out.println("executed01");
        dpGetter=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                System.out.println("executed02");
                System.out.println("qwertyuiop1234 : "+result.getResultCode());
                if(result.getResultCode()==-1){
                    Bundle myBundle=result.getData().getExtras();
                    Uri imageUri;
                    Bitmap imgBitmap=(Bitmap) myBundle.get("data");
                    WeakReference<Bitmap> wr=new WeakReference<>(Bitmap.createScaledBitmap(imgBitmap,
                                    imgBitmap.getWidth(),imgBitmap.getHeight(),false)
                            .copy(Bitmap.Config.RGB_565,true));
                    Bitmap am=wr.get();
                    imageUri=saveImage(am,getContext());
                    System.out.println("imageUri"+imageUri);
                    String uriImage=String.valueOf(imageUri);
                    System.out.println("mobile"+phone);
                    System.out.println("imageUri"+uriImage);

                    userDetails.collection("users").document(phone).update("displayPicture",uriImage);

                    userDetails.collection("users").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot doc= task.getResult();
                                String image=(String) doc.get("displayPicture");
                                if(image.equals("")){ }
                                else{
                                    Uri imgUri=Uri.parse(image);
                                    profilePic.setImageURI(imgUri);
                                }
                            }
                        }
                    });
                }



            }
        });

        System.out.println("qwertyuiop : "+userDetails.collection("users").document(phone).get());

        return view;
    }

    private Uri saveImage(Bitmap am, Context context) {
        Uri uri=null;
        File images=new File(context.getCacheDir(),"images");
        try{
            System.out.println("qwertyuiop : "+uri);
            images.mkdirs();
            System.out.println("qwertyuiop2 : "+uri);
            File file=new File(images,"IMG"+System.currentTimeMillis()+".jpg");
            System.out.println("qwertyuiop3 : "+uri);
            FileOutputStream fos=new FileOutputStream(file);
            System.out.println("qwertyuiop4 : "+uri);
            am.compress(Bitmap.CompressFormat.JPEG,100,fos);
            System.out.println("qwertyuiop5 : "+uri);
            fos.flush();
            fos.close();

            uri= FileProvider.getUriForFile(context.getApplicationContext(),
                    "com.example.uber_clone"+".provider",file);
            System.out.println("qwertyuiop66 : "+uri);
            System.out.println("imageUri@ : "+uri);

        }catch (Exception e){
            System.out.println("qwerty"+e.getMessage());
        }

        return uri;
    }
}
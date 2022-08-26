package com.sih.adminkeyblue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sih.adminkeyblue.databinding.ActivityMaps2Binding;

import java.util.Objects;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityMaps2Binding binding;
    String tel=null;
    ImageView verifystatus,imageshow;
    TextView name , dob, blood, gender,mobile,status;
    DatabaseReference unsafe= FirebaseDatabase.getInstance().getReference("unsafe"),user = FirebaseDatabase.getInstance().getReference("users");
    TextView name1,name2,emernum1,emernum2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hook();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void hook() {
        name = (TextView) findViewById(R.id.name);
        dob = (TextView) findViewById(R.id.dob);
        blood = (TextView) findViewById(R.id.blood);
        gender = (TextView) findViewById(R.id.gender);
        mobile = (TextView) findViewById(R.id.mobile);
        status = (TextView) findViewById(R.id.status);
        verifystatus = (ImageView) findViewById(R.id.verifystatus);
        imageshow = (ImageView) findViewById(R.id.imageshow);
        //emergency
        name1 = findViewById(R.id.relationName1);
        name2 = findViewById(R.id.relationName2);
        emernum1 = findViewById(R.id.emer1);
        emernum2 = findViewById(R.id.emer2);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        unsafe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Double lati = 0.0,longi = 0.0;
                    String type = null;
                    for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                        if(Objects.equals(snapshot2.getKey(), "lat"))lati=snapshot2.getValue(Double.class);
                        if(Objects.equals(snapshot2.getKey(), "long"))longi=snapshot2.getValue(Double.class);
                        if(Objects.equals(snapshot2.getKey(), "type"))type=snapshot2.getValue(String.class);

                    }
                    LatLng latLng = new LatLng(lati,longi);
                    //Toast.makeText(MapsActivity2.this, ""+latLng.toString(), Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(snapshot1.getKey())).setTag(type);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                tel=marker.getTitle();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),12f));
                mobile.setText(tel);
                gender.setText(""+marker.getTag().toString());
                unsafe.child(tel).child("verified").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Objects.equals(snapshot.getValue(String.class), "yes")) {
                            status.setText("verified");
                            verifystatus.setAlpha(0f);

                        }
                        else {
                            status.setText("unverified");
                            verifystatus.setAlpha(1f);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                user.child(tel).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot3) {

                        for(DataSnapshot snapshot4 : snapshot3.getChildren()){
                            if(Objects.equals(snapshot4.getKey(), "Full Name"))name.setText(snapshot4.getValue(String.class));
                            if(Objects.equals(snapshot4.getKey(), "BloodGroup"))blood.setText(snapshot4.getValue(String.class));
                            if(Objects.equals(snapshot4.getKey(), "Date of Birth"))dob.setText(snapshot4.getValue(String.class));

                            if(Objects.equals(snapshot4.getKey(), "Relation01")){
                                for(DataSnapshot snapshot5 : snapshot4.getChildren()){
                                    String namer = snapshot5.getKey();
                                    String num = (String) snapshot5.getValue();
                                    name1.setText(namer + "'s Number");
                                    emernum1.setText(num);
                                }
                            }
                            if(Objects.equals(snapshot4.getKey(), "Relation02")){
                                for(DataSnapshot snapshot5 : snapshot4.getChildren()){
                                    String namer = snapshot5.getKey();
                                    String num = (String) snapshot5.getValue();
                                    name2.setText(namer + "'s Number");
                                    emernum2.setText(num);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });
    }
    public void calluser(View view){
        Intent intent_04 = new Intent(Intent.ACTION_DIAL);
        intent_04.setData(Uri.parse("tel:"+tel));
        startActivity(intent_04);
    }

    public void callemergency(View view){
        TextView txtview = (TextView) view;
        Intent intent_04 = new Intent(Intent.ACTION_DIAL);
        tel = txtview.getText().toString();
        intent_04.setData(Uri.parse("tel:"+txtview.getText().toString()));
        startActivity(intent_04);
    }

    public void verifyuser(View view){
        unsafe.child(tel).child("verified").setValue("yes");
        Toast.makeText(this, "User has been Verified!", Toast.LENGTH_SHORT).show();
    }
    public void showimage(View view){
        Intent intent=new Intent(MapsActivity2.this,
                imageshow.class);
        intent.putExtra("tel",tel);
        startActivity(intent);
    }

}
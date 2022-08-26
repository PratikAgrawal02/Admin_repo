package com.sih.adminkeyblue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView status,type,departments;
    String[] statuslist = {"True","False"};
    String[] typelist = {"Earthquake","Flood","Thunderstorm","Landslide","Commissioner office","Collector Office"};
    ArrayAdapter<String> typeadapter,statusadapter;
    String statusfinal=null,typefinal=null,depart=null;
    EditText name, password,news,lat,longi;
    Button update;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setadapters();

        update = findViewById(R.id.update);
        name =(EditText)findViewById(R.id.userName);

        lat = (EditText)findViewById(R.id.lat);
        longi = (EditText)findViewById(R.id.longi);
        password = (EditText)findViewById(R.id.password);
        news = (EditText)findViewById(R.id.news);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namef= name.getText().toString(),passwordf=password.getText().toString();

                if(namef.isEmpty() || passwordf.isEmpty() || statusfinal==null || typefinal==null || depart==null){
                    Toast.makeText(MainActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.child("Admin").child(depart).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(passwordf.equals(String.valueOf(task.getResult().getValue()))){
                                Toast.makeText(MainActivity.this, "Query sent", Toast.LENGTH_SHORT).show();
                                adddatatofirebase();

                            }
                            else Toast.makeText(MainActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });




    }
    public void clearall(){
        name.setText("");
        news.setText("");
        password.setText("");
        lat.setText("");
        longi.setText("");
    }
    public void openmap(View view){
        Intent intent=new Intent(MainActivity.this,
                MapsActivity.class);
        startActivityForResult(intent,0);
    }
    public void openmap2(View view){
        Intent intent=new Intent(MainActivity.this,
                MapsActivity2.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == Activity.RESULT_OK){
           // Toast.makeText(this, ""+String.valueOf(data.getDoubleExtra("latitude",0)), Toast.LENGTH_SHORT).show();
            lat.setText(String.valueOf(data.getDoubleExtra("latitude",0)));
            longi.setText(String.valueOf(data.getDoubleExtra("longitude",0)));
        }
    }

    public void adddatatofirebase(){

        databaseReference.child("disaster").child("lat").setValue(Double.parseDouble(lat.getText().toString()));
        databaseReference.child("disaster").child("long").setValue(Double.parseDouble(longi.getText().toString()));

        databaseReference.child("disaster").child("name").setValue(typefinal);
        databaseReference.child("disaster").child("news").setValue(news.getText().toString());
        if(Objects.equals(statusfinal, "True"))   databaseReference.child("disaster").child("mode").setValue(true);
        else  databaseReference.child("disaster").child("mode").setValue(false);

        clearall();
    }
    public void setadapters() {

        type=findViewById(R.id.type);
        typeadapter= new ArrayAdapter<String>(this,R.layout.list_item,typelist);
        type.setAdapter(typeadapter);

        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, ""+typeadapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
                typefinal = typeadapter.getItem(i).toString();
            }
        });

        departments = findViewById(R.id.organization);
        departments.setAdapter(typeadapter);
        departments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                depart = typeadapter.getItem(i).toString();
            }
        });

        status = findViewById(R.id.status);
        statusadapter = new ArrayAdapter<String>(this,R.layout.list_item,statuslist);
        status.setAdapter(statusadapter);

        status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                statusfinal = statusadapter.getItem(i).toString();
            }
        });

    }
}
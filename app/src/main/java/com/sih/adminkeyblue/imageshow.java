package com.sih.adminkeyblue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class imageshow extends AppCompatActivity {

    ImageView firebaseimage;
    ProgressDialog progressDialog;
    StorageReference storageReference,storageReference2;
    String tel;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageshow);
        videoView = (VideoView)findViewById(R.id.video);
        firebaseimage = (ImageView) findViewById(R.id.firebaseimage);
        tel = getIntent().getExtras().getString("tel","default");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Retrieving File....");
        progressDialog.show();
        storageReference2 = FirebaseStorage.getInstance().getReference("video/"+tel);

        storageReference = FirebaseStorage.getInstance().getReference("images/"+tel);
        try {
            File localfile = File.createTempFile("tempfile",".mp4");
            storageReference2.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if(progressDialog.isShowing())progressDialog.dismiss();
                    videoView.setVideoPath(localfile.getAbsolutePath());
                    videoView.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File localfile = File.createTempFile("tempfile",".jpeg");
            storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    if(progressDialog.isShowing())progressDialog.dismiss();
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    firebaseimage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing())progressDialog.dismiss();

                    Toast.makeText(imageshow.this, "No image found for this user", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
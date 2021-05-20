package com.inducesmile.lets_walk;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class activity_userDetails extends AppCompatActivity {

    private AutoCompleteTextView name, height, weight;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mstorageRef;
    private static final int RESULT_LOAD_IMAGE= 1;


    String inputName;
    String inputHeight;
    String inputWeight;

    String default_name;
    String default_Height;
    String default_Weight;

    Uri selectedImage;
    TextView profileImg;
    ImageView imagetoUpload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);
        load_data();

        name=findViewById(R.id.userName);
        height=findViewById(R.id.userHeight);
        weight=findViewById(R.id.userWeight);
        save=findViewById(R.id.save_button);
        firebaseAuth = FirebaseAuth.getInstance();
        profileImg=findViewById(R.id.change_photo);
        imagetoUpload=(ImageView) findViewById(R.id.image);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mstorageRef= FirebaseStorage.getInstance().getReference("Uploads");
        String userId = firebaseAuth.getCurrentUser().getUid();

        name.setText(default_name);
        height.setText(default_Height);
        weight.setText(default_Weight);

        mstorageRef.child(userId+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(activity_userDetails.this).load(imageURL).into(imagetoUpload);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputName = name.getText().toString().trim();
                inputHeight = height.getText().toString().trim();
                inputWeight = weight.getText().toString().trim();

                if(validateInput(inputName, inputHeight, inputWeight)) {
                    update_db(inputName);
                    save_data();
                    startActivity(new Intent(activity_userDetails.this, activity_home.class));
                }
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE&& resultCode==RESULT_OK&&data!=null){
            selectedImage = data.getData();
            imagetoUpload.setImageURI(selectedImage);
            upload_file(selectedImage);
        }
    }

    private void upload_file(Uri selectedImage) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference fileReference=mstorageRef.child(userId+".jpeg");
        fileReference.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(activity_userDetails.this,"Image Uploaded Successfully",Toast.LENGTH_LONG).show();
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        Map newUser= new HashMap();
                        newUser.put("ImageURL",taskSnapshot.getUploadSessionUri().toString());
                        mDatabase.child(userId).updateChildren(newUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity_userDetails.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void save_data() {
        SharedPreferences sharedPref = getSharedPreferences("userData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Name",inputName);
        editor.putString("Height",inputHeight);
        editor.putString("Weight",inputWeight);
        editor.apply();
    }

    public void load_data(){
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        default_name = sharedPreferences.getString("Name", "");
        default_Height=sharedPreferences.getString("Height","");
        default_Weight=sharedPreferences.getString("Weight","");
    }

    private void update_db(String inputName) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        Map newUser= new HashMap();
        newUser.put("Name",inputName);
        mDatabase.child(userId).updateChildren(newUser);
    }

    private boolean validateInput(String inName, String inHeight, String inWeight){

        if(inName.isEmpty()){
            name.setError("Name field is empty.");
            return false;
        }
        if(inHeight.isEmpty()){
            height.setError("Height field is empty.");
            return false;
        }
        if(inWeight.isEmpty()){
            weight.setError("Weight field is empty.");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


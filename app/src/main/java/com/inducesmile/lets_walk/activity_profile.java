package com.inducesmile.lets_walk;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class activity_profile extends AppCompatActivity {

    private ImageView how;
    private ImageView header_refer;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mstorageRef;
    private FirebaseUser user;
    TextView edit;
    TextView weight;
    TextView height;
    TextView name;
    TextView coins;
    TextView steps;

    String showHeight;
    String showWeight;
    String showName;
    int global_steps;
    int global_coins;
    ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        load_data();
        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        how=(ImageView)findViewById(R.id.how);
        header_refer=(ImageView)findViewById(R.id.refer1);
        edit=(TextView)findViewById(R.id.edit_profile_text);
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        weight=findViewById(R.id.userWeight);
        height=findViewById(R.id.userHeight);
        name=findViewById(R.id.name);
        steps=findViewById(R.id.global_steps);
        coins=findViewById(R.id.global_coins);
        profile_image=(ImageView) findViewById(R.id.image);
        String userId=user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mstorageRef= FirebaseStorage.getInstance().getReference("Uploads/").child(userId+".jpeg");

        weight.setText(showWeight);
        height.setText(showHeight);
        name.setText(showName);
        steps.setText(""+global_steps);
        coins.setText(""+global_coins);

        mstorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(activity_profile.this).load(imageURL).into(profile_image);
            }
        });

        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Intent intent1 = new Intent(activity_profile.this, activity_home.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_vouchers:
                        Intent intent2 = new Intent(activity_profile.this, activity_vouchers.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_friends:
                        Intent intent3 = new Intent(activity_profile.this, activity_friends.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_profile:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_profile.this,
                        activity_userDetails.class);
                startActivity(myIntent);
            }
        });

        header_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_profile.this,
                        activity_refer.class);
                startActivity(myIntent);

            }
        });

        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_profile.this,
                        activity_how_list.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.home != selectedItemId) {
            setHomeItem(activity_profile.this);
        } else {
            super.onBackPressed();
        }
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    public void load_data(){
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        showName=sharedPreferences.getString("Name","");
        showHeight=sharedPreferences.getString("Height","");
        showWeight=sharedPreferences.getString("Weight","");
        SharedPreferences sharedPreferences1=getSharedPreferences("sharedprefs",MODE_PRIVATE);
        global_coins=sharedPreferences1.getInt("text7",0);
        global_steps=sharedPreferences1.getInt("text8",0);
    }
}
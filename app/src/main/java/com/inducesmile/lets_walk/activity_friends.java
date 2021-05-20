package com.inducesmile.lets_walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class activity_friends extends AppCompatActivity {

    ImageView how;
    ImageView header_refer;
    private ListView listView;
    Button refer;
    TextView mSearchParams;

    //private List<User> mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        how=(ImageView)findViewById(R.id.how);
        header_refer=(ImageView)findViewById(R.id.refer1);
        refer=(Button)findViewById(R.id.refer);
        mSearchParams=findViewById(R.id.find);
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        hideSoftKeyboard();
        //TextChangeLitener();

        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        listView=(ListView)findViewById(R.id.friends_list);

        final String[] mobileArray = {"Dravid", "Dhoni", "Kedar", "Sachin", "Debeliers", "Rohit", "Chahal", "UV", "Kohli", "Jadeja"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , mobileArray);
        listView.setAdapter(adapter);


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
                        Intent intent1 = new Intent(activity_friends.this, activity_home.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_vouchers:
                        Intent intent2 = new Intent(activity_friends.this, activity_vouchers.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_friends:
                        break;
                    case R.id.navigation_profile:
                        Intent intent3 = new Intent(activity_friends.this, activity_profile.class);
                        startActivity(intent3);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            }
        });

        header_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_friends.this,
                        activity_refer.class);
                startActivity(myIntent);

            }
        });

        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_friends.this,
                        activity_refer.class);
                startActivity(myIntent);

            }
        });


        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_friends.this,
                        activity_how_list.class);
                startActivity(myIntent);
            }
        });
    }

    private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    /*private void searchForUsers(String keyword){
        mUsersList.clear();
        if(keyword.length()==0){

        }
        else {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            Query query=reference.child("Users").orderByChild("username").equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren());

                    updateUsersList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void updateUsersList() {
    }

    private void TextChangeLitener(){
        mUsersList=new ArrayList<>();
        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text=mSearchParams.getText().toString().toLowerCase(Locale.getDefault());
                searchForUsers(text);
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.home != selectedItemId) {
            setHomeItem(activity_friends.this);
        } else {
            super.onBackPressed();
        }
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}


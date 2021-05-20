package com.inducesmile.lets_walk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class activity_how_list extends AppCompatActivity {

    private ListView listView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_list);


        firebaseAuth=FirebaseAuth.getInstance();
        listView=(ListView)findViewById(R.id.how_list);

        final String[] how_list = {"Account", "How it works", "About", "Support", "Partner with Us", "Logout"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , how_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listName = how_list[position];
                if(listName=="Account"){
                    Intent intent = new Intent(activity_how_list.this, activity_profile.class);
                    startActivity(intent);
                }
                else if(listName=="How it works"){
                    Intent intent = new Intent(activity_how_list.this, activity_how1.class);
                    startActivity(intent);
                }
                else if(listName=="About"){
                    Intent intent = new Intent(activity_how_list.this, activity_profile.class);
                    startActivity(intent);
                }
                else if(listName=="Support"){
                    Intent intent = new Intent(activity_how_list.this, activity_profile.class);
                    startActivity(intent);
                }
                else if(listName=="Partner with us"){
                    Intent intent = new Intent(activity_how_list.this, activity_profile.class);
                    startActivity(intent);
                }
                else if(listName=="Logout"){
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(activity_how_list.this,activity_login.class));
                }
            }
        });
    }
}

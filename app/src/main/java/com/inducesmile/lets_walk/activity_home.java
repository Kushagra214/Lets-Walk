package com.inducesmile.lets_walk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class activity_home extends AppCompatActivity implements SensorEventListener, StepListener, RewardedVideoAdListener {

    public static final String SHARED_PREF="sharedprefs";
    public static final String TEXT_STEPS="text1";
    public static final String TEXT_STEP_COINS="text2";
    public static final String TEXT_REWARD_COINS="text3";
    public static final String TEXT_TOTAL_COINS="text4";
    public static final String TEXT_DISTANCE="text5";
    public static final String TEXT_CALORIES="text6";
    public static final String TEXT_GLOBAL_COINS="text7";
    public static final String TEXT_GLOBAL_STEPS="text8";
    public TextView steps;
    public TextView steps_coins;
    public TextView reward_coins;
    public TextView total_coins;
    public TextView distance;
    public TextView calories;

    String showHeight;
    String dis;
    float stride;

    private StepDetector simpleStepDetector;
    SensorManager sensorManager;
    Sensor accel;

    ImageView how;
    ImageView header_refer;
    ImageView arrow1;
    ImageView arrow2;
    Button get_rewards;
    Button spend_coins;
    Button summary;
    RewardedVideoAd rewardedVideoAd;

    //int back_steps;
    int num_step_coins;
    int num_reward_coins;
    int showSteps;
    int num_total_coins;
    float num_distance;
    int num_calories;
    int global_coins;
    int global_steps;
    private FirebaseAuth firebaseAuth;

    //receiver StepsReceiver;

    //Intent mServiceIntent;
    //private SensorService mSensorService;

    Context context;
    //Intent mServiceIntent;
    //private MyService mService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_home);

        //mService = new MyService();
        //mServiceIntent = new Intent(context, mService.getClass());

        //startService(mServiceIntent);

        //LocalBroadcastManager.getInstance(getApplication()).registerReceiver(StepsReceiver, new IntentFilter("StepsUpdate"));
        //showSteps=+back_steps;
        //save_data();
        load_data();

        firebaseAuth = FirebaseAuth.getInstance();

        steps=(TextView)findViewById(R.id.steps);
        reward_coins=(TextView)findViewById(R.id.reward_coins);
        steps_coins=(TextView)findViewById(R.id.step_coins);
        total_coins=(TextView)findViewById(R.id.textView6);
        distance=(TextView)findViewById(R.id.textView18);
        calories=(TextView)findViewById(R.id.textView16);
        how=(ImageView)findViewById(R.id.how);
        header_refer=(ImageView)findViewById(R.id.refer1);
        arrow1=(ImageView)findViewById(R.id.arrow1);
        arrow2=(ImageView)findViewById(R.id.arrow2);
        get_rewards=(Button)findViewById(R.id.rewards);
        spend_coins=(Button)findViewById(R.id.spend_coins);
        summary=(Button)findViewById(R.id.summary);

        final int a = !showHeight.equals("")?Integer.parseInt(showHeight) : 0;
        stride= (float)(a*0.43*0.00001);

        if (showSteps < 10) {
            steps.setText("000" + showSteps);
        } else if(showSteps>=10&&showSteps<100){
            steps.setText("00" + showSteps);
        } else if(showSteps>=100&&showSteps<999){
            steps.setText("0" + showSteps);
        } else{
            steps.setText(showSteps+"");}
        if(num_step_coins<10) {
            steps_coins.setText("0"+num_step_coins);
        }else {steps_coins.setText(num_step_coins+"");}
        if(num_reward_coins<10) {
            reward_coins.setText("0"+num_reward_coins);
        }else{reward_coins.setText(num_reward_coins+"");}
        total_coins.setText("0"+num_total_coins);
        if(num_calories<10){
        calories.setText("0"+num_calories);}
        else { calories.setText(""+num_calories);}
        dis=String.format("%.2f",num_distance);
        distance.setText(dis);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(Build.VERSION.SDK_INT>=19){
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        else {
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);



        //Mobile Ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //Bottom banner ad
        AdView mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        get_rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_reward_coins<=10)
                {
                    if(rewardedVideoAd.isLoaded()){
                        rewardedVideoAd.show();
                    }
                    else {
                        loadAds();
                        rewardedVideoAd.show();
                    }
                }
                else {
                     Toast.makeText(getApplicationContext(),"You have completed the daily limit.\nCome back tomorrow for more rewards.",Toast.LENGTH_LONG).show();
                }
                }
        });


        MobileAds.initialize(this,"ca-app-pub-3940256099942544/5224354917");
        //ca-app-pub-8310301726521010/4079970064
        rewardedVideoAd=MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadAds();


        //Bottom Navigation
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_vouchers:
                        Intent intent1 = new Intent(activity_home.this, activity_vouchers.class);
                        save_data();
                        startActivity(intent1);
                        break;
                    case R.id.navigation_friends:
                        Intent intent2 = new Intent(activity_home.this, activity_friends.class);
                        save_data();
                        startActivity(intent2);
                        break;
                    case R.id.navigation_profile:
                        Intent intent3 = new Intent(activity_home.this, activity_profile.class);
                        save_data();
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
                Intent myIntent = new Intent(activity_home.this,
                        activity_refer.class);
                startActivity(myIntent);

            }
        });

        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_home.this,
                        activity_how_list.class);
                startActivity(myIntent);
            }
        });

        arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_home.this,
                        activity_how1.class);
                startActivity(myIntent);
            }
        });

        arrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_home.this,
                        activity_how2.class);
                startActivity(myIntent);
            }
        });

        spend_coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_home.this,
                        activity_vouchers.class);
                startActivity(myIntent);
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity_home.this,
                        activity_summary.class);
                startActivity(myIntent);
            }
        });
        load_data();
    }


   /*class receiver extends BroadcastReceiver{

       @Override
       public void onReceive(Context context, Intent intent) {
           intent.getIntExtra("Back_Steps",back_steps);
       }
   }*/


    //Steps Count Part 2
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector = new StepDetector();
            simpleStepDetector.registerListener(this);
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
        else if(event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR){

            ++showSteps;
            ++global_steps;

            num_calories = (int) (0.04*showSteps);
            if(num_calories<10)
                calories.setText("0"+num_calories);
            else
                calories.setText(num_calories+"");

            num_distance=stride*showSteps;
            dis=String.format("%.2f",num_distance);
            distance.setText(dis);

            if(showSteps<=10000) {
                if(showSteps%1000==0)
                {
                    num_total_coins++;
                    global_coins++;
                    if(num_total_coins<10){
                        total_coins.setText("0"+num_total_coins);
                    }
                    else {
                        total_coins.setText(""+num_total_coins);
                    }
                }
                num_step_coins = (int)Math.floor(showSteps / 1000);
                if (num_step_coins < 10) {
                    steps_coins.setText("0" + num_step_coins);
                } else {
                    steps_coins.setText(num_step_coins + "");
                }
            }

            if (showSteps < 10) {
                steps.setText("000" + showSteps);
            }
            else if(showSteps>=10&&showSteps<100){
                steps.setText("00" + showSteps);
            }
            else if(showSteps>=100&&showSteps<=999){
                steps.setText("0" + showSteps);
            }
            else{
                steps.setText(showSteps+"");
            }
        }
    }

    @Override
    public void step(long timeNs) {
        ++showSteps;
        ++global_steps;

        num_calories = (int) (0.04*showSteps);
        if(num_calories<10)
            calories.setText("0"+num_calories);
        else
            calories.setText(num_calories+"");

        num_distance=stride*showSteps;
        dis=String.format("%.2f",num_distance);
        distance.setText(dis);

        if(showSteps<=10000) {
            if(showSteps%1000==0)
            {
                num_total_coins++;
                global_coins++;
                if(num_total_coins<10){
                total_coins.setText("0"+num_total_coins);
                }
                else {
                    total_coins.setText(""+num_total_coins);
                }
            }
            num_step_coins = (int)Math.floor(showSteps / 1000);
            if (num_step_coins < 10) {
                steps_coins.setText("0" + num_step_coins);
            } else {
                steps_coins.setText(num_step_coins + "");
            }
        }

        if (showSteps < 10) {
            steps.setText("000" + showSteps);
        }
        else if(showSteps>=10&&showSteps<100){
            steps.setText("00" + showSteps);
        }
        else if(showSteps>=100&&showSteps<=999){
            steps.setText("0" + showSteps);
        }
        else{
            steps.setText(showSteps+"");
        }
    }



    //Pressing Back button (Exit app)
    @Override
    public void onBackPressed() {
        save_data();
        finishAffinity();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(mServiceIntent);
        //unregisterReceiver(StepsReceiver);

    }

    //Rewarded Ads methods
    private void loadAds(){
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",new AdRequest.Builder().build());
    }
    @Override
    public void onRewardedVideoAdLoaded() {

    }
    @Override
    public void onRewardedVideoAdOpened() {

    }
    @Override
    public void onRewardedVideoStarted() {

    }
    @Override
    public void onRewardedVideoAdClosed() {
        loadAds();
    }
    @Override
    public void onRewarded(com.google.android.gms.ads.reward.RewardItem rewardItem) {
        Toast.makeText(getApplicationContext(),"Congratulations! You have earned a reward",Toast.LENGTH_LONG).show();
        num_reward_coins++;
        num_total_coins++;
        global_coins++;
        if(num_total_coins<10){
            total_coins.setText("0"+num_total_coins);
        }
        else {
            total_coins.setText(""+num_total_coins);
        }

        if (num_reward_coins < 10) {
            reward_coins.setText("0" + num_reward_coins);
        }
        else {
            reward_coins.setText(num_reward_coins+"");
        }
    }
    @Override
    public void onRewardedVideoAdLeftApplication() {
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadAds();
    }
    @Override
    public void onRewardedVideoCompleted() {
    }
    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }
    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }


    //Saving steps, step_coins and reward_coins data
    public void save_data(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(TEXT_STEPS,showSteps);
        editor.putInt(TEXT_STEP_COINS,num_step_coins);
        editor.putInt(TEXT_REWARD_COINS,num_reward_coins);
        editor.putInt(TEXT_TOTAL_COINS,num_total_coins);
        editor.putFloat(TEXT_DISTANCE,num_distance);
        editor.putInt(TEXT_CALORIES,num_calories);
        editor.putInt(TEXT_GLOBAL_COINS,global_coins);
        editor.putInt(TEXT_GLOBAL_STEPS,global_steps);
        editor.apply();
    }

    //Loading steps, step_coins and reward_coins data
    public void load_data(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        showSteps=sharedPreferences.getInt(TEXT_STEPS,0000);
        num_step_coins=sharedPreferences.getInt(TEXT_STEP_COINS,00);
        num_reward_coins=sharedPreferences.getInt(TEXT_REWARD_COINS,00);
        num_total_coins=sharedPreferences.getInt(TEXT_TOTAL_COINS,00);
        num_distance=sharedPreferences.getFloat(TEXT_DISTANCE,0);
        num_calories=sharedPreferences.getInt(TEXT_CALORIES,00);
        global_coins=sharedPreferences.getInt(TEXT_GLOBAL_COINS,0);
        global_steps=sharedPreferences.getInt(TEXT_GLOBAL_STEPS,0);
        SharedPreferences sharedPreferences1 = getSharedPreferences("userData", MODE_PRIVATE);
        showHeight = sharedPreferences1.getString("Height", "");
    }
}
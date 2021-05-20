package com.inducesmile.lets_walk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Service;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

//import static com.inducesmile.lets_walk.App_notification.Channel_ID;


//public class SensorService extends Service implements SensorEventListener, StepListener{

    /*private StepDetector simpleStepDetector;
    SensorManager sensorManager;
    Sensor accel;


    /*public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService(){

    }*

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Channel_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent=new Intent(this,activity_home.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification=new NotificationCompat.Builder(this,Channel_ID)
                .setContentTitle("Lets Walk")
                .setContentText("Lets Walk is currently running in background")
                .setSmallIcon(R.drawable.home)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }
    @Override
    public void step(long timeNs) {
        ++showSteps;

        if(showSteps<=10000) {
            if(showSteps%1000==0)
            {
                num_total_coins++;
                activity_home.total_coins.setText(""+num_total_coins);
            }
            num_step_coins = (int)Math.floor(showSteps / 1000);
            if (num_step_coins < 10) {
                activity_home.steps_coins.setText("0" + num_step_coins);
            } else {
                activity_home.steps_coins.setText(num_step_coins + "");
            }
        }

        if (showSteps < 10) {
            activity_home.steps.setText("000" + showSteps);
        }
        else if(showSteps>=10&&showSteps<100){
            activity_home.steps.setText("00" + showSteps);
        }
        else if(showSteps>=100&&showSteps<=999){
            activity_home.steps.setText("0" + showSteps);
        }
        else{
            activity_home.steps.setText(showSteps+"");
        }
    }*/




/**
 * Created by fabio on 30/01/2016.

public class SensorService extends Service implements SensorEventListener{
    //public int counter = 0;
    //int showsteps_background=0;
    boolean serviceStopped;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;
    private final Handler handler = new Handler();
    int currentStepsDetected;
    int stepCounter;
    int newStepCounter;
    int counter = 0;
    Intent intent;
    public static final String BROADCAST_ACTION = "SensorRestarterBroadcastReceiver";

    private StepDetector simpleStepDetector;
    SensorManager sensorManager;
    Sensor accel;

    /*@Override
    public void onCreate() {
        super.onCreate();

        // --------------------------------------------------------------------------- \\
        // ___ (2) create/instantiate intent. ___ \\
        // Instantiate the intent declared globally, and pass "BROADCAST_ACTION" to the constructor of the intent.
        intent = new Intent(BROADCAST_ACTION);
        // ___________________________________________________________________________ \\
    }*

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService(){

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("SensorService", "on start command () running");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounterSensor, 0);
        sensorManager.registerListener(this, stepDetectorSensor, 0);
        //super.onStartCommand(intent, flags, startId);

        serviceStopped = false;
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds

        //startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStopped = true;
        Log.i("EXIT", "ondestroy!");
        //Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        //sendBroadcast(broadcastIntent);
        //stoptimertask();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // STEP_COUNTER Sensor.
        // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int countSteps = (int) event.values[0];

            // -The long way of starting a new step counting sequence.-
            /**
             int tempStepCount = countSteps;
             int initialStepCount = countSteps - tempStepCount; // Nullify step count - so that the step cpuinting can restart.
             currentStepCount += initialStepCount; // This variable will be initialised with (0), and will be incremented by itself for every Sensor step counted.
             stepCountTxV.setText(String.valueOf(currentStepCount));
             currentStepCount++; // Increment variable by 1 - so that the variable can increase for every Step_Counter event.
             *

            // -The efficient way of starting a new step counting sequence.-
            if (stepCounter == 0) { // If the stepCounter is in its initial value, then...
                stepCounter = (int) event.values[0]; // Assign the StepCounter Sensor event value to it.
            }
            newStepCounter = countSteps - stepCounter; // By subtracting the stepCounter variable from the Sensor event value - We start a new counting sequence from 0. Where the Sensor event value will increase, and stepCounter value will be only initialised once.
        }

        // STEP_DETECTOR Sensor.
        // *** Step Detector: When a step event is detect - "event.values[0]" becomes 1. And stays at 1!
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectSteps = (int) event.values[0];
            currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.
        }

        Log.v("Service Counter", String.valueOf(newStepCounter));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void broadcastSensorValue() {
        Log.d("SensorService", "Data to Activity");
        // add step counter to intent.
        intent.putExtra("Counted_Step_Int", newStepCounter);
        //intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        // add step detector to intent.
        //intent.putExtra("Detected_Step_Int", currentStepsDetected);
        //intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(intent);
    }

    /*private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    /**
     * not needed

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    public void step(long timeNs) {
        ++showsteps_background;
    }*

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}*/
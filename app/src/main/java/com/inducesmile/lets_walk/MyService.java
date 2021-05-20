package com.inducesmile.lets_walk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyService extends Service implements SensorEventListener, StepListener {

    SensorManager sensorManager;
    private StepDetector simpleStepDetector;
    Sensor accel;

    int showSteps;
    Context context;

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        showSteps=0;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(Build.VERSION.SDK_INT>=19){
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        else {
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        onTaskRemoved(intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent=new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector = new StepDetector();
            simpleStepDetector.registerListener(this);
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
        else if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER) {
            ++showSteps;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        ++showSteps;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendDataToActivity(showSteps);
    }

    public void sendDataToActivity(int steps){
        Intent intent = new Intent("StepsUpdate");
        // You can also include some extra data.
        intent.putExtra("Back_Steps", steps);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

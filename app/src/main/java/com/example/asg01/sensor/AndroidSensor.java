package com.example.asg01.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;
import java.util.ArrayList;
public abstract class AndroidSensor implements SensorEventListener {
    private Context context;
    private int sensorType;
    private SensorManager sensorManager;
    private Sensor sensor;
    protected CustomEventSensor customEvent;

    public AndroidSensor(Context context, int sensorType) {
        this.context = context;
        this.sensorType = sensorType;
    }

    public void addCustomEventSensor(CustomEventSensor customEvent) {
        this.customEvent = customEvent;
    }

    public boolean sensorExist() {
        if (sensorManager == null && sensor == null) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(sensorType);
            return true;
        }
        return false;
    }

    public void startListening() {
        if (!sensorExist()) {
            return;
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stopListening() {
        if (!sensorExist() || sensorManager == null) {
            return;
        }
        sensorManager.unregisterListener(this, sensor);
    }
}

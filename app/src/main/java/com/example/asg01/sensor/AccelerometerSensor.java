package com.example.asg01.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class AccelerometerSensor extends AndroidSensor {
    public AccelerometerSensor(Context context) {
        super(context, Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        customEvent.moveShip(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Todo
    }
}

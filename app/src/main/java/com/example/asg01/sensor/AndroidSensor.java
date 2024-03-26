package com.example.asg01.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;
import java.util.ArrayList;
public abstract class AndroidSensor extends MeasurableSensor implements SensorEventListener {
    private Context context;
    private String sensorFeature;
    private SensorManager sensorManager;
    private Sensor sensor;

    public AndroidSensor(Context context, String sensorFeature, int sensorType) {
        super(sensorType);
        this.context = context;
        this.sensorFeature = sensorFeature;
    }

    @Override
    public boolean doesSensorExist() {
        return context.getPackageManager().hasSystemFeature(sensorFeature);
    }

    @Override
    public void startListening() {
        if (!doesSensorExist()) {
            return;
        }
        if (sensorManager == null && sensor == null) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(sensorType);
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void stopListening() {
        if (!doesSensorExist() || sensorManager == null) {
            return;
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!doesSensorExist()) {
            return;
        }
        if (event.sensor.getType() == sensorType) {
            if (onSensorValuesChanged != null) {
                onSensorValuesChanged.onValuesChanged(convertArrayToList(event.values));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý
    }

    private List<Float> convertArrayToList(float[] array) {
        List<Float> list = new ArrayList<>();
        for (float value : array) {
            list.add(value);
        }
        return list;
    }
}

package com.example.asg01.sensor;

import java.util.List;

public abstract class MeasurableSensor {
    protected int sensorType;
    protected OnSensorValuesChangedListener onSensorValuesChanged;

    public MeasurableSensor(int sensorType) {
        this.sensorType = sensorType;
    }

    public abstract boolean doesSensorExist();
    public abstract void startListening();
    public abstract void stopListening();

    public void setOnSensorValuesChangedListener(OnSensorValuesChangedListener listener) {
        this.onSensorValuesChanged = listener;
    }

    public interface OnSensorValuesChangedListener {
        void onValuesChanged(List<Float> values);
    }
}
package com.example.asg01.receiver;

import android.net.NetworkCapabilities;

public interface InternetHandleEvent {
    void lostInternet();
    void capabilitiesChanged(NetworkCapabilities networkCapabilities);
}

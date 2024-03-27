package com.example.asg01.receiver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.asg01.MainActivity;
import com.example.asg01.R;

public class InternetReceiver extends BroadcastReceiver {
    private Context context;
    private Dialog dialog;
    private InternetHandleEvent handleEvent;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_reload_internet);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                showDialog();
                handleEvent.lostInternet();
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    dismissDialog();
                    handleEvent.capabilitiesChanged(networkCapabilities);

                }
            }
        });
    }

    private void showDialog() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!dialog.isShowing()) {
                    dialog.show();
                    Toast.makeText(context, "Internet is disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissDialog() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    Toast.makeText(context, "Internet is connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public InternetReceiver addEvent(InternetHandleEvent handleEvent) {
        this.handleEvent = handleEvent;
        return this;
    }
}

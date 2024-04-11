package com.example.asg01.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.asg01.R;

public class InternetReceiver extends BroadcastReceiver {
    private Dialog dialog;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_reload_internet);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;
        }

        if (InternetConnectivity.isConnected(context)) {
            dialog.cancel();
        } else {
            dialog.show();
        }
    }
}

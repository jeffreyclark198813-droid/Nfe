package com.labtools.nfc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * HostApduService implementation that starts as a foreground service to remain active.
 * Uses ApduProcessor to handle APDUs and returns responses.
 */
public class HceService extends HostApduService {

    private static final String CHANNEL_ID = "nfc_lab_hce_channel";
    private static final int NOTIFICATION_ID = 0x1001;

    private final ApduProcessor processor = new ApduProcessor();

    @Override
    public void onCreate() {
        super.onCreate();
        // Create notification channel and start foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NFC Lab HCE",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }

        Notification.Builder nb = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);

        Notification notif = nb
                .setContentTitle("NFC Lab HCE")
                .setContentText("Host Card Emulation service is running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        startForeground(NOTIFICATION_ID, notif);
    }

    @Nullable
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        return processor.process(apdu);
    }

    @Override
    public void onDeactivated(int reason) {
        // handle deactivation if necessary
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
package com.labtools.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple Activity to show NFC/HCE status and link to NFC settings.
 */
public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private Button btnNfcSettings;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.status);
        btnNfcSettings = findViewById(R.id.btn_nfc_settings);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        refreshStatus();

        btnNfcSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            if (intent.resolveActivity(getPackageManager()) == null) {
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        if (nfcAdapter == null) {
            statusText.setText("NFC NOT AVAILABLE");
            btnNfcSettings.setEnabled(false);
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            statusText.setText("NFC DISABLED");
            btnNfcSettings.setEnabled(true);
            return;
        }

        try {
            CardEmulation.getInstance(nfcAdapter)
                    .setPreferredService(this, new android.content.ComponentName(this, HceService.class));
            statusText.setText("HCE ACTIVE (Preferred service set)");
        } catch (Exception e) {
            statusText.setText("HCE READY (could not set preferred service): " + e.getMessage());
        }
        btnNfcSettings.setEnabled(false);
    }
}
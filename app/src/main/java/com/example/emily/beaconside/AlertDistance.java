package com.example.emily.beaconside;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by jennifer9759 on 2017/8/31.
 */

public class AlertDistance extends AppCompatActivity {
    String bName;
    String bAlert;
    TextView alert_bName;
    TextView alert_bAlert;
    MediaPlayer mediaPlayer;
    Vibrator myVibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_item);
        Bundle params = getIntent().getExtras();
        if (params != null) {
            bAlert = params.getString("bAlertDistance");
            bName = params.getString ("bName");

        }
        alert_bName = (TextView) findViewById(R.id.alert_bName);
        alert_bAlert = (TextView) findViewById(R.id.alert_bAlert);
        alert_bName.setText("\""+bName+"\"");
        alert_bAlert.setText(bAlert+"m");
        Toast.makeText(this, "alert: "+bAlert, Toast.LENGTH_SHORT).show();
        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.start();
        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(10000);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        myVibrator.cancel();
        mediaPlayer.pause();
        mediaPlayer.reset();
        finish();
    }

    public void onBackPressed(View view) {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        myVibrator.cancel();
        mediaPlayer.pause();
        mediaPlayer.reset();
        finish();
    }

    public static double tryParse(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

}

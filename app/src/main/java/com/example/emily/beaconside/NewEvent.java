package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class NewEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
    }
    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }
}

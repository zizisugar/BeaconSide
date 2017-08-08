package com.example.emily.beaconside;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by jennifer9759 on 2017/8/8.
 */
public class Compass extends AppCompatActivity implements SensorEventListener {
    /* compass */
    private float currentDegree = 0f;// record the angle turned
    private SensorManager mSensorManager;// device sensor manager

    /* calculate direction */
    private float minRSSI = 1000000;
    private float turntoTarget = 0;

    /* view component*/
    private ImageView image;
    private TextView itemName;
    private TextView itemDistance;
    private TextView itemDegree;
    /* bluetooth */
    BluetoothMethod bluetooth = new BluetoothMethod();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        /* intent取得傳遞過來的item名稱 */
        Bundle bundle = this.getIntent().getExtras();
        String name = bundle.getString("itemName"); // 接受要搜尋的藍牙裝置名稱
        String address = bundle.getString("itemAddress"); // 接受要搜尋的藍牙裝置地址
        /* compass */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// initialize your android device sensor capabilities

        /* bluetooth */
        bluetooth.BTinit(this);
        bluetooth.getStartSearchItem(address); // 指定要搜尋的藍牙裝置地址

        /* view component */
        image = (ImageView) findViewById(R.id.imageViewCompass);
        itemName = (TextView) findViewById(R.id.itemName);
        itemName.setText(name + "" + address);
        itemDistance = (TextView) findViewById(R.id.itemDistance);
        itemDegree = (TextView) findViewById(R.id.itemDegree);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        super.onDestroy();
        bluetooth.stopSearch();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        itemDistance.setText(bluetooth.getDistance() + " cm");
        itemDegree.setText(degree + " degree");

        /* direction */
        if( minRSSI > Math.abs(bluetooth.getRssi())){
            minRSSI = (float)Math.abs(bluetooth.getRssi());
            turntoTarget = -currentDegree;//N:0, E:+
        }

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree+turntoTarget,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    public void onBackPressed() {
        bluetooth.stopSearch();
    }
}

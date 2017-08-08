package com.example.emily.beaconside;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * Created by jennifer9759 on 2017/8/8.
 */
public class Compass extends AppCompatActivity implements SurfaceHolder.Callback,SensorEventListener {
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
    /* camera */
    private ToggleButton cameraBtn;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    private final String tag = "VideoServer";
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
        /* camera */
        setCamera();
    }

    private void setCamera() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraBtn=  (ToggleButton) findViewById(R.id.toggleButton);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 當按鈕第一次被點擊時候響應的事件
                if (cameraBtn.isChecked()) {
                    Toast.makeText(getBaseContext(), "開啟相機", Toast.LENGTH_SHORT).show();
                    start_camera();
//                    surfaceView.setVisibility(View.VISIBLE);
//                    image.setVisibility(View.GONE);
                }
                // 當按鈕再次被點擊時候響應的事件
                else {
                    Toast.makeText(getBaseContext(), "關閉相機", Toast.LENGTH_SHORT).show();
                    stop_camera();
//                    surfaceView.setVisibility(View.GONE);
//                    image.setVisibility(View.VISIBLE);
                }
            }
        });
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

        if (degree <=(turntoTarget+15) && degree>=(turntoTarget-15)){
            image.setVisibility(View.VISIBLE);
        }
        else
            image.setVisibility(View.INVISIBLE);
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

    private void start_camera()
    {
        try{
            camera = Camera.open();
        }catch(RuntimeException e){
            Log.e(tag, "init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        //modify parameter
        param.setPreviewFrameRate(20);
        //param.setPreviewSize(176,144);
//        param.setPreviewFormat();
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e) {
            Log.e(tag, "init_camera: " + e);
            return;
        }
    }

    private void stop_camera()
    {
        camera.stopPreview();
        camera.release();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

}

package com.example.emily.beaconside;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.powenko.ifroglab_bt_lib.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class BluetoothMethod implements ifrog.ifrogCallBack,SensorEventListener {


    public boolean myStatusBT=true, firstOpenBT=true;
    /* 運用library */
    private ifrog mifrog;
    public ArrayList<String> Names = new ArrayList<String>();
    public ArrayList<String> Address = new ArrayList<String>();
    public ArrayList<Double> Distance = new ArrayList<Double>();
    public ArrayList<String> Information = new ArrayList<String>();

    /* 調整distance */
    private double count = 0;
    private double distanceTotal = 0;
    double tempdis = 0;

    /* 藍芽 */
    public final int REQUEST_ENABLE_BT = 18;
    private boolean firstOpen = true;
    /* 方位 */
    private SensorManager mSensorManager;// device sensor manager
    private float minRSSI = 1000000;
    private float turntoTarget = 0;
    /* 呼叫藍牙方法的Activity */
    Context mContext;
    /* public 藍牙資訊 */
    public ArrayList<String> mac = new ArrayList<String>(Arrays.asList("84:EB:18:7A:5B:80","D0:39:72:DE:DC:3A","1C:BA:8C:28:8B:5F","laptop"));
    public ArrayList<String> myDeviceDistance = new ArrayList<>(Collections.nCopies(mac.size(),"Out of Range"));
    public float currentDegree= 0f;
    public double currentDistance=0;
    public String bluetoothFunction = ""; // 目前要使用的藍牙功能
    public String currentItem = "D0:39:72:DE:DC:3A";

    public void getStartSearch(Context context, Long time){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(firstOpenBT || !myStatusBT){
            if (!mBluetoothAdapter.isEnabled()) {//要求開啟藍芽的視窗
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }else{
                mifrog.scanLeDevice(myStatusBT,time);//true
            }
            firstOpenBT = false;
        }else{
            mifrog.scanLeDevice(myStatusBT,time);//true
        }

    }

    public void stopSearch(){
        myStatusBT = false;
        mifrog.scanLeDevice(myStatusBT,3600000);
    }

    public void BTinit(Context context){//藍芽初始化動作
        mifrog=new ifrog();
        mifrog.setTheListener(this);//設定監聽->CallBack(當有什麼反應會有callback的動作)->新增SearchFindDevicestatus, onDestroy
        mContext = context;
        //取得藍牙service，並把這個service交給此有藍芽的設備(BLE)。有些人有藍芽的設備不見得有藍芽的軟體。// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        if (mifrog.InitCheckBT(bluetoothManager) == null) {
            Toast.makeText(context,"this Device doesn't support Bluetooth BLE", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
            return;
        }
        getStartSearch(mContext,new Long(36000));
    }


    public void bluetoothStop() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        mifrog.BTSearchStop();
    }

    public double calculateDistance(int rssi){
        /*   d = 10^((abs(RSSI) - A) / (10 * n))  */
        double result = 0;

        //if(count>15){
        if(count>15){
            tempdis = distanceTotal/count;
            count = 0;
            distanceTotal = 0;
        }
        else{
            float txPower = -59;//hard coded power value. Usually ranges between -59 to -65
            if(rssi == 0){
                result = -1.0;
            }
            double ratio = rssi*1.0/txPower;
            if (ratio < 1.0) {
                result =  Math.pow(ratio,10);
            }
            else{
                double distance = (0.89976)*Math.pow(ratio,7.7095) + 0.111;
                result =  distance;
            }
            count ++;
            distanceTotal += result;
        }

        result = Math.round(result*10);
        return result;
    }

    @Override
    public void BTSearchFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        switch (bluetoothFunction) {
            case "searchItem":
                searchDevice(device,rssi);
                searchItem(device,currentItem,rssi);
                break;
            case "searchDevice":
                searchDevice(device,rssi);
                break;
            case "myItemDistance":
                searchDevice(device,rssi);
                myItemDistance(mac);
                break;
            default:
                searchDevice(device,rssi);
                break;
        }

    }

    @Override
    public void BTSearchFindDevicestatus(boolean arg0) {//arg0:true/false，代表有沒有在找
        if(arg0==false){
            Toast.makeText(mContext,"Stop Search", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"Start Search",  Toast.LENGTH_SHORT).show();
        }
    }

    public void myItemDistance(ArrayList<String> mac) {
        int i,j;
        String d="Out of Range";
        for (i=0; i<mac.size(); i++) {
            for(j=0;j<Address.size();j++){
                if(mac.get(i).equals(Address.get(j))){
                    d = String.valueOf(Distance.get(j));
                    break;
                }
                else
                    d = "Out of Range";
            }
            myDeviceDistance.set(i,d);
        }
    }

    public void searchItem(BluetoothDevice device,String item,int rssi) {
        Toast.makeText(mContext,item +" || "+device.getAddress(), Toast.LENGTH_SHORT).show();
        if(item.equals(device.getAddress())){
            currentDistance = calculateDistance(rssi);
            Toast.makeText(mContext,item +"is "+currentDistance+"cm away", Toast.LENGTH_SHORT).show();
            /* direction */
            if( minRSSI > Math.abs(rssi)){
                minRSSI = Math.abs(rssi);
                turntoTarget = -currentDegree;//N:0, E:+
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

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

//        if(page == 2) {
//            // Start the animation
//            image.startAnimation(ra);
//        }
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void searchDevice(BluetoothDevice device,int rssi){
        String t_address= device.getAddress();//有找到裝置的話先抓Address
        int index=0;
        boolean t_NewDevice=true;
        for(int i=0;i<Address.size();i++){
            String t_Address2=Address.get(i);
            if(t_Address2.compareTo(t_address)==0){//如果address和列表中的address一模一樣
                t_NewDevice=false;//登記說他不是新的device
                index=i;//把index記起來
                break;
            }
        }
        if(device.getName() != null){

            if(t_NewDevice==true){//如果是新的device
                Address.add(t_address);
                //null can appear
                Names.add(device.getName());
                Distance.add(calculateDistance(rssi));
                Information.add(
                        "Device : "+device.getName()+
                                "\nAddress : "+t_address+
                                "\nRssi : "+Integer.toString(rssi)+
                                "\nDistance : "+Double.toString(calculateDistance(rssi))
                );
                Toast.makeText(mContext,"Find new device"+ device.getName(), Toast.LENGTH_SHORT).show();
            }else{//如果不是新的device
                Names.set(index,device.getName());
                Distance.set(index,calculateDistance(rssi));
                Information.add(
                        "Device : "+device.getName()+
                                "\nAddress : "+t_address+
                                "\nRssi : "+Integer.toString(rssi)+
                                "\nDistance : "+Double.toString(calculateDistance(rssi))+"cm"
                );
            }
        }
    }
}
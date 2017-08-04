package com.example.emily.beaconside;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.powenko.ifroglab_bt_lib.ifrog;

import java.util.ArrayList;


public class BluetoothMethod extends AppCompatActivity implements ifrog.ifrogCallBack{


    private boolean myStatusBT=true, firstOpenBT=true;
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
    final int REQUEST_ENABLE_BT = 18;
    private boolean firstOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*經過了dialog卻還是沒開啟 關掉check*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED){
            myStatusBT = false;
        }
        else{
            myStatusBT = true;
        }
        getStartSearch();
    }

    public void getStartSearch(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(firstOpenBT || !myStatusBT){
            if (!mBluetoothAdapter.isEnabled()) {//要求開啟藍芽的視窗
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }else{
                mifrog.scanLeDevice(myStatusBT,3600000);//true
            }
            firstOpenBT = false;
        }else{
            mifrog.scanLeDevice(myStatusBT,3600000);//true
        }

    }

    public void stopSearch(){
        myStatusBT = false;
        mifrog.scanLeDevice(myStatusBT,3600000);
    }

    public void BTinit(){//藍芽初始化動作
        mifrog=new ifrog();
        mifrog.setTheListener(this);//設定監聽->CallBack(當有什麼反應會有callback的動作)->新增SearchFindDevicestatus, onDestroy

        //取得藍牙service，並把這個service交給此有藍芽的設備(BLE)。有些人有藍芽的設備不見得有藍芽的軟體。// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mifrog.InitCheckBT(bluetoothManager) == null) {
            Toast.makeText(this,"this Device doesn't support Bluetooth BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        getStartSearch();
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
                Names.add(device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//抓名字然後放進列表
                Distance.add(calculateDistance(rssi));
                Information.add(
                        "Device : "+device.getName()+
                                "\nAddress : "+t_address+
                                "\nRssi : "+Integer.toString(rssi)+
                                "\nDistance : "+Double.toString(calculateDistance(rssi))
                );
//                testValues = Names.toArray(new String[Names.size()]);
//                testValues2 =Address.toArray(new String[Address.size()]);
//                testValues3 = Information.toArray(new String[Information.size()]);
            }else{//如果不是新的device
                Names.set(index,device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//更改device名字，RSSI:藍芽4.0裡面可以知道訊號強度
                Distance.set(index,calculateDistance(rssi));
                Information.add(
                        "Device : "+device.getName()+
                                "\nAddress : "+t_address+
                                "\nRssi : "+Integer.toString(rssi)+
                                "\nDistance : "+Double.toString(calculateDistance(rssi))+"cm"
                );
//                testValues = Names.toArray(new String[Names.size()]);//放進array
//                testValues3 = Information.toArray(new String[Information.size()]);
            }
        }
    }

    @Override
    public void BTSearchFindDevicestatus(boolean arg0) {//arg0:true/false，代表有沒有在找
        if(arg0==false){
            Toast.makeText(getBaseContext(),"Stop Search", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"Start Search",  Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh() {

    }

}
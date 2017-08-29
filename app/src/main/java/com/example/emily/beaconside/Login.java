package com.example.emily.beaconside;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;



public class Login extends AppCompatActivity implements View.OnClickListener {

    CallbackManager callbackManager;
//    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    AccessToken accessToken;
    private Button btn_add;
    private Button btn_friends;
    private Button btn_search;

    public static String uEmail;
    private String uId ;
    private String uName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_friends.setOnClickListener(this);
        accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken!=null){

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        //當RESPONSE回來的時候
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Toast.makeText(Login.this,"Get Token",Toast.LENGTH_SHORT).show();
                            //讀出姓名、ID、網頁連結
                            try {
                                Log.e("Already Log in", "Already Log in");
                                Toast.makeText(Login.this,"Already Log in",Toast.LENGTH_SHORT).show();
                                uId=(String) object.get("id");
                                uName=(String) object.get("name");
                                uEmail=(String) object.get("email");
                                /**換頁到Main**/
                                Intent intent = new Intent();
                                intent.setClass(Login.this,MainActivity.class);
                                //傳遞變數
                                intent.putExtra("uEmail",uEmail);
                                startActivity(intent);
                                finish();
                                /******/
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Failed","Failed");
                                Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                        }});
        }


/**
 *         FB登入按鈕，要求使用者權限，能要求的有email、friends、profile
 *         未做 : 登入時將資料寫進資料庫
 */
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().logInWithReadPermissions(
                Login.this,
                Arrays.asList("email,user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名、ID、網頁連結
                                try {
                                    uId=(String) object.get("id");
                                    uName=(String) object.get("name");
                                    uEmail=(String) object.get("email");
                                    addUser();
                                    /**換頁到Main**/
                                    Intent intent = new Intent();
                                    intent.setClass(Login.this,MainActivity.class);
                                    //傳遞變數
                                    intent.putExtra("uEmail",uEmail);
                                    startActivity(intent);
                                    finish();
                                    /******/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }});
                //包入你想要得到的資料，送出 request
                Bundle parameters = new Bundle();
                parameters.putString("fields" , "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();

            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {
            }
        });
/**
 *   HTTP Request取得資料
 */

    }


/**
     *  分享到user塗鴉牆，需另外要求權限，未使用dialog方塊
     */
    private void publishImage(){
        Bitmap image = BitmapFactory.decodeResource(getResources(),     R.mipmap.ic_launcher);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
//                分享出的內容
                .setCaption("Welcome To Facebook Photo Sharing on steroids!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void search(){
        class Search extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(Login.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(Login.this,s,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    JSONArray friend_list=object.getJSONObject("friends").getJSONArray("data");
//                                    for (int i = 0; i < counter ; i++) {
//                                        Object jsonID = friend_list.getJSONObject(i).get("id");
//                                        jID[i] = jsonID.toString();
//                                    }
                                    Object jsonID = friend_list.getJSONObject(3).get("id");
                                    uId=jsonID.toString();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "friends");
                request.setParameters(parameters);
                request.executeAsync();

                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequestParam(Config.URL_SEARCH, uId);
                return res;

            }
        }

        Search ae = new Search();
        ae.execute();
    }
    private void addUser(){

        class AddUser extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(Login.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(Login.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                //params.put(傳給php檔的post id,傳遞的變數)
                params.put("uEmail",uEmail);
                params.put("uId",uId);
                params.put("uName",uName);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_USER, params);
                return res;
            }
        }

        AddUser ae = new AddUser();
        ae.execute();
    }
    private  void friendsList(){
//        List = (ListView) findViewById(R.id.list);
//        if(accessToken!=null) {
//            GraphRequest request = GraphRequest.newMeRequest(
//                    accessToken,
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//
//                            try {
////                                String friends = object.getString("friends");
//                                //好友資料 個數
//                                JSONArray friend_list=object.getJSONObject("friends").getJSONArray("data");
//                                int counter = friend_list.length();
//                                ArrayList<HashMap<String , String>> list = new ArrayList<>();
//                                String[] jname = new String[counter];
//                                String[] jID = new String[counter];
//
//                                for (int i = 0; i < counter ; i++) {
//                                    Object jsonName = friend_list.getJSONObject(i).get("name");
//                                    jname[i] = jsonName.toString();
//                                    Object jsonID = friend_list.getJSONObject(i).get("id");
//                                    jID[i] = jsonID.toString();
//
//                                    HashMap<String , String> hashMap = new HashMap<>();
//                                    hashMap.put("name" , jname[i]);
//                                    hashMap.put("id" , jID[i]);
//                                    list.add(hashMap);
//                                }
//
//
//                                ListAdapter adapter = new SimpleAdapter(.Login.this , list , android.R.layout.simple_list_item_2
//                                        ,new String[]{"name" , "id"},new int[]{android.R.id.text1 , android.R.id.text2});
//                                List.setAdapter(adapter);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "friends");
//            request.setParameters(parameters);
//            request.executeAsync();
//        }
        Intent intent = new Intent();
        intent.setClass(Login.this, Friends.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        if(v == btn_friends){
            friendsList();
        }
        if(v == btn_search){
            search();
        }
    }}
//

package com.example.emily.beaconside;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Friends extends AppCompatActivity {
    CallbackManager callbackManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    private ListView List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        List = (ListView) findViewById(R.id.list);
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,



                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            try {
//                                String friends = object.getString("friends");
                                //好友資料 個數
                                JSONArray friend_list = object.getJSONObject("friends").getJSONArray("data");
                                int counter = friend_list.length();
                                ArrayList<HashMap<String, String>> list = new ArrayList<>();
                                String[] jname = new String[counter];
                                String[] jID = new String[counter];

                                for (int i = 0; i < counter; i++) {
                                    Object jsonName = friend_list.getJSONObject(i).get("name");
                                    jname[i] = jsonName.toString();
                                    Object jsonID = friend_list.getJSONObject(i).get("id");
                                    jID[i] = jsonID.toString();

                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("name", jname[i]);
                                    hashMap.put("id", jID[i]);
                                    list.add(hashMap);
                                }

                                android.widget.ListAdapter adapter = new SimpleAdapter(Friends.this, list, android.R.layout.simple_list_item_2
                                        , new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
                                List.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "friends");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }
}

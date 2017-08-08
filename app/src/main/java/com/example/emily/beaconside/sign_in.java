package com.example.emily.beaconside;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class sign_in extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);




//        將文字變成連結
//        forgive_link =(TextView)findViewById(R.id.forgiveAccount);
//        forgive_link.setClickable(true);
//        forgive_link.setMovementMethod(LinkMovementMethod.getInstance());
//        String text = "<a href='http://www.google.com'> Google </a>";
//        forgive_link.setText(Html.fromHtml(text));
    }


    public void signInBT(View v){
        startActivity(new Intent(sign_in.this, MainActivity.class));//same as following two

    }
}

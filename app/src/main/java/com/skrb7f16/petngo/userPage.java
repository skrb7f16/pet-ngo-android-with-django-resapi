package com.skrb7f16.petngo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class userPage extends AppCompatActivity {
    String TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
    }

    public void goDonation(View view){

        Intent intent=new Intent(this,donation.class);
        startActivity(intent);
    }
    public void goReport(View view){

        Intent intent=new Intent(this,Report.class);
        startActivity(intent);
    }

    public void logOut(View view){
        SharedPreferences sharedPreferences=getSharedPreferences("auth",MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.remove("token");
        edit.apply();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
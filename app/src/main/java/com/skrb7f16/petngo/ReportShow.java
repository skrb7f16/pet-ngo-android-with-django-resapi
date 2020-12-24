package com.skrb7f16.petngo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.skrb7f16.petngo.Models.ReportModel;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportShow extends AppCompatActivity {

    ReportModel r;
    TextView textView;
    ImageView imageView;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_show);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent intent=getIntent();
        String temp=intent.getStringExtra("Report");
        try {
            JSONObject json=new JSONObject(temp);
            r=new ReportModel(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView=findViewById(R.id.typeVal);
        textView.setText(r.getType());
        textView=findViewById(R.id.breedVal);
        textView.setText(r.getBreed());
        textView=findViewById(R.id.ReportVal);
        textView.setText(r.getReport());
        Uri uri=Uri.parse("https://res.cloudinary.com/skrb7f16/"+r.getPic());
        imageView=findViewById(R.id.reportImg);
        imageView.setImageBitmap(getBitmapFromURL(uri.toString()));
    }


    public  Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
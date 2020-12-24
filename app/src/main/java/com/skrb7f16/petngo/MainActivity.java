package com.skrb7f16.petngo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    TextView usernameTextView,passwordTextView;
    SharedPreferences sharedPreferences;
    String TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameTextView=findViewById(R.id.username);
        passwordTextView=findViewById(R.id.password);
        sharedPreferences=getSharedPreferences("auth",MODE_PRIVATE);
        TOKEN =sharedPreferences.getString("token","");
        if(TOKEN!=""){
            Intent intent=new Intent(this,userPage.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View view) throws JSONException {
        String url = "https://donation-app-pet.herokuapp.com/auth-token/";
        JSONObject body=new JSONObject();
        body.put("username", usernameTextView.getText().toString());
        body.put("password", passwordTextView.getText().toString());
        final String mRequestBody = body.toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sharedPreferences=getSharedPreferences("auth",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                Log.d("meow", "hello"+response.toString());
                try {
                    TOKEN=response.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.putString("token",TOKEN);
                editor.apply();
                Intent intent=new Intent(MainActivity.this,userPage.class);
                startActivityForResult(intent,100);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(error.networkResponse.statusCode>=500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                else if(error.networkResponse.statusCode>=400 && error.networkResponse.statusCode<500){
                    Toast.makeText(getApplicationContext(), "Credential wrong", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Check ur network connection",Toast.LENGTH_SHORT);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

        };

        requestQueue.add(jsonObjectRequest);

    }


    public void signup(View view){
        Intent signupIntent=new Intent(Intent.ACTION_VIEW);
        signupIntent.setData( Uri.parse("https://donation-app-pet.herokuapp.com/"));
        startActivity(signupIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==401){
            Toast.makeText(this,"Please Login",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("meow", String.valueOf(requestCode));
            this.finishAffinity();
        }

    }
}
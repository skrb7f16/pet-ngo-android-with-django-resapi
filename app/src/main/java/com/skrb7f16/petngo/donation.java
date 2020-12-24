package com.skrb7f16.petngo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.skrb7f16.petngo.Models.Donations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class donation extends AppCompatActivity {
    String Token;
    ListView listView;
    List<Donations> donationsList;
    ArrayList<String>donationArrayList;
    ArrayList<Integer>ids=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        listView=findViewById(R.id.donationList);
        SharedPreferences sharedPreferences=getSharedPreferences("auth",MODE_PRIVATE);
        Token=sharedPreferences.getString("token","");
        donationsList= new ArrayList<>();
        donationArrayList=new ArrayList<>();
        getDonations();
    }

    void getDonations(){
        String url="https://donation-app-pet.herokuapp.com/api/user/donations";
        RequestQueue requestQueue= Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    return;
                }
                try {
                    for (int i=0;i<response.length();i++){
                        JSONObject json=response.getJSONObject(i);
                        Donations d=new Donations();
                        d.setAmount(json.getInt("amount"));
                        d.setDoneOn(json.getString("doneOn"));
                        d.setId(json.getInt("id"));
                        donationsList.add(d);
                    }
                    setDonation();

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("meow","he"+error.getMessage());
                Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                finishActivity(404);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization","Token "+Token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    void setDonation() throws ParseException {
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date date;
        for(Donations d:donationsList){
            date=sd.parse(d.getDoneOn());
            donationArrayList.add("Amount : "+d.getAmount()+"\n"+"Done On "+date);
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,donationArrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext()," "+donationsList.get(position).getAmount(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void donateFunc(View view) throws JSONException {
        String url="https://donation-app-pet.herokuapp.com/api/user/donations";
        JSONObject body=new JSONObject();
        final EditText editText=findViewById(R.id.donationAmount);
        if(editText.getText().length()==0){
            editText.setError("Cant be empty");
            return;
        }
        int amount=parseInt(editText.getText().toString());
        body.put("amount",amount);
        final String mRequestBody = body.toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                    editText.setText(null);
                    listView.setAdapter(null);
                    donationArrayList.clear();
                    donationsList.clear();
                    getDonations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Server error",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Token "+Token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


}
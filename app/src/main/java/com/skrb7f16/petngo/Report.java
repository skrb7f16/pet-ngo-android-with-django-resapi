package com.skrb7f16.petngo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.skrb7f16.petngo.Models.Donations;
import com.skrb7f16.petngo.Models.ReportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends AppCompatActivity {
    ImageView imgView;
    EditText editText;
    Bitmap bitmapMain;
    String Token;
    ListView listView;
    List<ReportModel> reportList=new ArrayList<>();
    int flagForImage=0;
    final ArrayList<String>reportArrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SharedPreferences sharedPreferences=getSharedPreferences("auth",MODE_PRIVATE);
        Token=sharedPreferences.getString("token","");
        listView=findViewById(R.id.reports);
        getReport();
    }


    public void uploadImage(View view){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri path=data.getData();
            try {
                bitmapMain= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                imgView=findViewById(R.id.imageView);
                imgView.setImageBitmap(bitmapMain);
                imgView.setVisibility(View.VISIBLE);
                flagForImage=1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void getReport(){
        String url="https://donation-app-pet.herokuapp.com/api/user/reports";
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
                        ReportModel reportModel=new ReportModel();
                        reportModel.setReport(json.getString("report"));
                        reportModel.setBreed(json.getString("breed"));
                        reportModel.setType(json.getString("type"));
                        reportModel.setDoneOn(json.getString("doneOn"));
                        reportModel.setId(json.getInt("id"));
                        reportModel.setPic(json.getString("pic"));
                        reportList.add(reportModel);
                    }
                    setReport();

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("meow","he"+error.getMessage()+error.networkResponse.statusCode);
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

   public void setReport() throws ParseException {
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date date;
        for(ReportModel d:reportList){
            date=sd.parse(d.getDoneOn());
            reportArrayList.add("Type : "+d.getType()+"\n"+"Done On "+date);
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,reportArrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext()," "+reportList.get(position).getType(),Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject=reportList.get(position).serializedObject();
                    Intent intent=new Intent(getApplicationContext(),ReportShow.class);
                    intent.putExtra("Report",jsonObject.toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void upload(View view) throws JSONException {
        String url="https://donation-app-pet.herokuapp.com/api/user/reports";
        final String report=getValue(editText,R.id.reportText);
        if(report.length()==0){
            Toast.makeText(getApplicationContext(),"Report cannnot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        final String type=getValue(editText,R.id.typeText);
        if(type.length()==0){
            Toast.makeText(getApplicationContext(),"Type cannnot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        final String breed=getValue(editText,R.id.breedText);
        if(breed.length()==0){
            Toast.makeText(getApplicationContext(),"Breed cannnot be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            emptyAll();
                            listView.setAdapter(null);
                            reportArrayList.clear();
                            reportList.clear();
                            getReport();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("meow",error.networkResponse.allHeaders.toString());
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("report",report );
                params.put("type",type );
                params.put("breed",breed );
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if(flagForImage==1) {
                    long imagename = System.currentTimeMillis();
                    params.put("pic", new DataPart(imagename + ".png", imageToByteArray(bitmapMain)));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token "+Token);
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }
    public byte[] imageToByteArray(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte=byteArrayOutputStream.toByteArray();
        return imgByte;
    }

    public String getValue(EditText e,int id){
        e=(EditText) findViewById(id);
        String temp=e.getText().toString();
        return temp;
    }

    public void emptyAll(){
        editText=(EditText)findViewById(R.id.reportText);
        editText.setText(null);
        editText=(EditText)findViewById(R.id.breedText);
        editText.setText(null);
        editText=(EditText)findViewById(R.id.typeText);
        editText.setText(null);

    }

}






package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {
    private TextView txt_address;
    private TextView txt_people;
    private TextView txt_size;
    private TextView txt_user;
    private TextView txt_job;
    private Button btn_join;
    double wasteLatitude;
    double wasteLongtitude;
    String userID;
    int wasteID;
    String waste_address;
    //String urlString = (String) getText(R.string.hienngu);
    String wasteURL = "http://10.141.128.59/androidwebservice/wasteLocation.php";
    String insertJoinURL ="http://10.141.128.59/androidwebservice/insertJoin.php";
    String urlWasteJoin = "http://10.141.128.59/androidwebservice/WasteJoin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join3);

        getWasteID(wasteURL);
        checkJoin(urlWasteJoin);
        reflect();
        Bundle bundle = getIntent().getExtras();
        wasteLatitude = bundle.getDouble("wasteLatitude");
        wasteLongtitude = bundle.getDouble("wasteLongtitude");
        waste_address = bundle.getString("waste_address");
        userID = bundle.getString("userID");

        if (bundle != null) {
            if (bundle.getString("waste_address") != null) {
                txt_address.setText(bundle.getString("waste_address"));
            }
            if (bundle.getString("waste_people") != null) {
                txt_people.setText(bundle.getString("waste_people"));
            }
            if (bundle.getString("waste_size") != null) {
                txt_size.setText(bundle.getString("waste_size"));
            }
            if (bundle.getString("userJob") != null) {
                txt_job.setText(bundle.getString("userJob"));
            }
            if (bundle.getString("username") != null) {
                txt_user.setText(bundle.getString("username"));
            }
        }
        joinData();
    }


    public void joinData() {
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("wasteLat",wasteLatitude);
                data.putExtra("wasteLong",wasteLongtitude);
                data.putExtra("wasteAddress",waste_address);
                data.putExtra("userID",userID);
                data.putExtra("wasteID",wasteID);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    private void checkJoin(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        int waste_id = object.getInt("waste_id");
                        String volunteer_id = object.getString("volunteer_id");
                        if(wasteID == waste_id &&  userID.equals(volunteer_id)){
                            Toast.makeText(JoinActivity.this, "This address can not join", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                insertData(insertJoinURL);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getWasteID(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        double wasteLocation_latitude = Double.parseDouble(object.getString("waste_latitude"));
                        double wasteLocation_longtitude = Double.parseDouble(object.getString("waste_longtitude"));
                        if(wasteLatitude == wasteLocation_latitude &&  wasteLongtitude == wasteLocation_longtitude){
                            wasteID = object.getInt("waste_id");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private void insertData(String insertJoinURL){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insertJoinURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Date today=new Date(System.currentTimeMillis());
                SimpleDateFormat timeFormat= new SimpleDateFormat("yyyy-MM-dd");
                String date = timeFormat.format(today.getTime());
                Map<String,String> params = new HashMap<>();
                params.put("waste_id",String.valueOf(wasteID));
                params.put("volunteer_id",userID);
                params.put("date",date);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void reflect(){
        txt_address = (TextView) findViewById(R.id.txt_address);
        txt_people = (TextView) findViewById(R.id.txt_people);
        txt_size = (TextView) findViewById(R.id.txt_size);
        txt_job = (TextView) findViewById(R.id.txt_job);
        txt_user = (TextView) findViewById(R.id.txt_user);
        btn_join = (Button) findViewById(R.id.btn_join);
    }
}

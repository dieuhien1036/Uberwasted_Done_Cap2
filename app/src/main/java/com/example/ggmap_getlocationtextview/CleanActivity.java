package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CleanActivity extends AppCompatActivity {
    public ArrayList<JoinClean> joinclean = new ArrayList<>();
    String userID;
    ListView listView;
    AdapterListView adapterListView;
    joinDialog.BottomSheetListener mListener;
    String urlDelete = "http://192.168.1.4/androidwebservice/deleteJoin.php";
    String url = "http://192.168.1.4/androidwebservice/WasteJoin.php";
    String urlCheckin = "http://192.168.1.4/androidwebservice/checkIn.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        listView = (ListView) findViewById(R.id.listview);
        adapterListView = new AdapterListView(this, R.layout.row_list_waste, mListener, joinclean, urlCheckin);
        listView.setAdapter(adapterListView);
        getDataJoinID(url);
    }


    public void deleteJoin(final String join_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDelete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("success")) {
                    Toast.makeText(CleanActivity.this, "Cancel Success!", Toast.LENGTH_SHORT).show();
                    getDataJoinID(url);
                } else {
                    Toast.makeText(CleanActivity.this, "Cancel Fail. Please confirm again!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("join_id", join_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getDataJoinID(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                joinclean.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String waste_id = object.getString("waste_id");
                        double wasteJoinLat = object.getDouble("waste_latitude");
                        double wasteJoinLong = object.getDouble("waste_longtitude");
                        String wasteAddress = object.getString("waste_address");
                        String join_id = object.getString("join_id");
                        String volunteer_id = object.getString("volunteer_id");
                        if (volunteer_id.equals(userID)) {
                            joinclean.add(new JoinClean(join_id, waste_id, wasteJoinLat, wasteJoinLong, wasteAddress));
                            Log.e("UUU",volunteer_id+" " + waste_id + " "+userID);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterListView.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
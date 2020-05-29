package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckCheckinRequi extends AppCompatActivity implements View.OnClickListener {
    private double current_latitude;
    private double currennt_longtitude;
    String urlGetData = "http://192.168.43.54/ub/getWaste.php";

    //    Button btnclick ;
//    private TextView tx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_database);
//        tx =  (TextView) findViewById(R.id.textRead);
//        btnclick = (Button) findViewById(R.id.btnclick);
//        btnclick.setOnClickListener(this);
        Intent intent = getIntent();
        current_latitude = intent.getDoubleExtra("latitude", 0);
        currennt_longtitude = intent.getDoubleExtra("longitude", 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnclick: {
                checkWasteForCheckin("http://192.168.43.54/ub/getWaste.php", current_latitude, currennt_longtitude);
            }
        }
    }

//    private void docDL(String urlClassification){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlClassification, null
//                , new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Toast.makeText(CheckCheckinRequi.this, response.toString(),Toast.LENGTH_LONG).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(CheckCheckinRequi.this, error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue.add(jsonArrayRequest);
//    }

    private void checkWasteForCheckin(String url, final Double current_latitude, final double current_longtitude) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(1);
                                Double lattitude = object.getDouble("waste_latitute");
                                Double longtitude = object.getDouble("waste_longtitude");
                                if ((current_latitude < lattitude + 2.0 && current_latitude > lattitude - 2)
                                        && (current_longtitude < longtitude + 2.0 && current_longtitude > longtitude - 2.0)) {
                                    // cho pheps check in

                                    break;
                                } else {

                                    //load laij
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
//    class getData extends AsyncTask<String, String , String > {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .readTimeout(15, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
//        @Override
//        protected String doInBackground(String... strings) {
//            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
//            builder.urlClassification(strings[0]);
//            Request request = builder.build();
//            try {
//                Response response = okHttpClient.newCall(request).execute();
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if(s != null){
//                tx.setText(s);
//            }else{
//                Toast.makeText(CheckCheckinRequi.this, "Fail", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}

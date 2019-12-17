package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class PasswordChanger extends AppCompatActivity {

    ImageButton btnSend;
    EditText etNewpassword;
    EditText etRenewpassword;
    String Newpassword, Renewpassword, urlUpdate, emailName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordchanger);
        urlUpdate = "http://192.168.1.9/forgot/forgot.php";
        btnSend = this.findViewById(R.id.btn_Change);
        etNewpassword = this.findViewById(R.id.et_Newpassword);
        etRenewpassword = this.findViewById(R.id.et_Renewpassword);

        Newpassword = etNewpassword.getText().toString();
        Renewpassword = etRenewpassword.getText().toString();

        Intent intent = getIntent();
        emailName = intent.getStringExtra("eName");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload(urlUpdate);
            }
        });
    }
    private void upload(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("SUCCESS")){
                            Toast.makeText(PasswordChanger.this, etNewpassword.getText().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PasswordChanger.this, MainActivity.class));
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PasswordChanger.this, "lozz", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("emailName",emailName);
                params.put("newPassword",etNewpassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
/*
        private void UpdatePassword(String url) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.trim().equals("SUCCESS")) {
                        Toast.makeText(PasswordChanger.this, "Password Changed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PasswordChanger.this, MainActivity.class));
                    } else
                        Toast.makeText(PasswordChanger.this, "Failed to chang2e password", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(PasswordChanger.this, "Failed to change password1", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                        params.put("emailName",emailName);
                    params.put("newPassword",Newpassword);

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    /*
        void MakePostRequest() {

            Map<String, String> params = new HashMap();
            params.put("emailName", emailName);
            params.put("newPassword", Newpassword);

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, urlUpdate, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response.equals("SUCCESS")){
                        Toast.makeText(PasswordChanger.this,"Password Changed",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PasswordChanger.this,MainActivity.class));
                    }
                    else
                        Toast.makeText(PasswordChanger.this,"Failed to change password",Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(PasswordChanger.this,"Failed to change password (Volley)",Toast.LENGTH_LONG).show();
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);
        }

    void MakePostRequest1() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://192.168.1.9/Donjudgeplz/forgot.php";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("emailName", emailName);;
            jsonBody.put("newPassword", Newpassword);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */

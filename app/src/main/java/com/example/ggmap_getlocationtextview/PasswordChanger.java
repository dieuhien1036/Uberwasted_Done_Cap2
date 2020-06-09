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
        urlUpdate = "http://192.168.1.4/Donjudgeplz/forgot.php";
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
                            startActivity(new Intent(PasswordChanger.this, ForgotActivity.class));
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
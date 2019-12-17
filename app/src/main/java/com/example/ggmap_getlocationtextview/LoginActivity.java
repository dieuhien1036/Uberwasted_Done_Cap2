package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity {
    EditText edt_Email;
    EditText edt_Pass;
    ImageButton bnt_Login;
    LinearLayout linearLayout;
    Button btn_ForgotPassword;
    String url ="http://192.168.1.6/androidwebservice/login.php";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        AnhXa();
        sharedPreferences = getSharedPreferences("DataLogin",MODE_PRIVATE);
       if(!sharedPreferences.getString("email","").isEmpty() && !sharedPreferences.getString("password","").isEmpty()){
            Intent mapActivityIntent = new Intent(LoginActivity.this, MapsActivity.class);
            mapActivityIntent.putExtra("userID",sharedPreferences.getString("userID",""));
            mapActivityIntent.putExtra("username",sharedPreferences.getString("username",""));
            mapActivityIntent.putExtra("dateOfBirth",sharedPreferences.getString("dateOfBirth",""));
            mapActivityIntent.putExtra("userScore",sharedPreferences.getString("userScore",""));
            mapActivityIntent.putExtra("userJob",sharedPreferences.getString("userJob",""));
            mapActivityIntent.putExtra("userGender",sharedPreferences.getString("userGender",""));
            startActivity(mapActivityIntent);
        }
        bnt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmptyFormat() == true) {
                    checkLogin(url);
                }
            }
        });
        btn_ForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(LoginActivity.this,MainActivity.class);
               Log.e("Dat ngu","Hien dep");
               startActivity(intent);
            }
        });

    }

    private boolean checkEmptyFormat(){
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        boolean OK = true;
        String email = edt_Email.getText().toString().trim();
        String password = edt_Pass.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this,"Input full information",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.matches(EMAIL_REGEX) == false){
            Toast.makeText(LoginActivity.this,"Invalid email",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 6 || password.length() > 20) {
            Toast.makeText(LoginActivity.this, "Password must less than 6 and more than 20", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void checkLogin(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Log.e("BBB","Vao day");
                        String email = object.getString("volunteer_email");
                        String password = object.getString("volunteer_password");
                        String userID = object.getString("volunteer_id");
                        String firstname = object.getString("volunteer_firstName");
                        String lastname = object.getString("volunteer_lastName");
                        String dateOfBirth = object.getString("volunteer_birthDate");
                        String userJob = object.getString("volunteer_job");
                        String userGender  = object.getString("volunteer_gender");
                        String userScore = object.getString("volunteer_score");
                        Log.e("BBB1",email + " " + password +" "+userID);
                        if(email.equals(edt_Email.getText().toString().trim()) == false || password.equals(edt_Pass.getText().toString()) == false){
                            if(email.equals(edt_Email.getText().toString().trim()) == false) {
                                Toast.makeText(LoginActivity.this, "Wrong Email", Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                Toast.makeText(LoginActivity.this, "Wrong Pasword", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }else{

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("email");
                            editor.remove("password");
                            editor.remove("userID");
                            editor.remove("username");
                            editor.remove("dateOfBirth");
                            editor.remove("userJob");
                            editor.remove("userGender");
                            editor.remove("userScore");
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.putString("userID",userID);
                            editor.putString("username",firstname+" "+lastname);
                            editor.putString("dateOfBirth",firstname+" "+lastname);
                            editor.putString("userJob",userJob);
                            editor.putString("userGender",userGender);
                            editor.putString("userScore",userScore);
                            editor.commit();

                            Intent mapActivityIntent = new Intent(LoginActivity.this, MapsActivity.class);
                            mapActivityIntent.putExtra("userID",userID);
                            mapActivityIntent.putExtra("username",firstname+" "+lastname);
                            mapActivityIntent.putExtra("dateOfBirth",dateOfBirth);
                            mapActivityIntent.putExtra("userScore",userScore);
                            mapActivityIntent.putExtra("userJob",userJob);
                            mapActivityIntent.putExtra("userGender",userGender);
                            startActivity(mapActivityIntent);
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
                Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void AnhXa(){
        edt_Email = (EditText) findViewById(R.id.edtEmail);
        edt_Pass = (EditText) findViewById(R.id.edtPass);
        bnt_Login = (ImageButton) findViewById(R.id.btn_Login);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        btn_ForgotPassword = (Button) findViewById(R.id.btn_ForgotPassword);
    }
}

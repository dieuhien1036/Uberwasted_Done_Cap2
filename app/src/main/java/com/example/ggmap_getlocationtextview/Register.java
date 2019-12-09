package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
//    String ip;

    private EditText firstname, lastname, email, password, confirmPassword;
    private ImageButton imageButton_register;
    private RequestQueue requestQueue;
    private static final String URL = "http://10.5.243.89/register/register.php";
    private StringRequest request;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "[a-zA-Z0-9]{1,256}"
            );
    private static final Pattern NAME_PATTERN =
            Pattern.compile(
                    "[a-zA-Z]{1,256}"
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        WifiManager manager= (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        ip=Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        firstname = findViewById(R.id.editText_firstName);
        lastname = findViewById(R.id.editText_lastName);
        email = findViewById(R.id.editText_email);
        password = findViewById(R.id.editText_password);
        confirmPassword = findViewById(R.id.editText_confirmPassword);
        imageButton_register = findViewById(R.id.imageButton_register);
        requestQueue = Volley.newRequestQueue(this);
        imageButton_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRegister();
                //Intent intent = new Intent(Register.this, LoginActivity.class);
                //startActivity(intent);

            }
        });


    }

    private void checkRegister() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString();
        String firstNameInput = firstname.getText().toString();
        String lastNameInput = lastname.getText().toString();
        if (firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must type all inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NAME_PATTERN.matcher(firstNameInput).matches()) {
            Toast.makeText(getApplicationContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NAME_PATTERN.matcher(lastNameInput).matches()) {
            Toast.makeText(getApplicationContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordInput.contains(" ")) {
            Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((password.getText().toString()).length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be 6 -20 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Password and Confirm Password don't match", Toast.LENGTH_SHORT).show();
            return;
        }
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("firstname", firstname.getText().toString());
                hashMap.put("lastname", lastname.getText().toString());
                hashMap.put("email", email.getText().toString().trim());
                hashMap.put("password", password.getText().toString());

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

}

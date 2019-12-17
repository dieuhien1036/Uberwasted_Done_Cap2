package com.example.ggmap_getlocationtextview;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChangeProfile extends AppCompatActivity implements View.OnClickListener {
    private String gd = "";
    private int id_Received = 1;
    private String getData="userID";
    private EditText edtFName;
    private EditText edtJob;
    private EditText edtLName;
    private EditText edtBirthDate;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private TextView txtEmail;
    private ImageButton btnSave;
    private ImageView ava;
    private String urlGetData = "http://192.168.1.6/ub/getUser.php";
    private String urlUpload = "http://192.168.1.6/ub/updateProfile.php";
    private String idReceived;

    private static final Pattern NAME_PATTERN =
            Pattern.compile(
                    "[a-zA-Z]"
            );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        reflect();
        if(rbMale.isChecked()){
             gd = "Male";
        }
        else{
             gd= "Female";
        }
        btnSave.setOnClickListener(this);
        edtBirthDate.setOnClickListener(this);

        Intent intent = getIntent();
        idReceived = intent.getStringExtra(getData);
        Log.d("AAAA","id1:"+String.valueOf(idReceived));

        id_Received = Integer.parseInt(idReceived.toString());
        Log.d("AAAA","id2:"+id_Received);
        getUserData(urlGetData, id_Received);
    }
    private void reflect(){
        edtFName = (EditText) findViewById(R.id.edt_firstName);
        edtLName = (EditText) findViewById(R.id.edt_lastName);
        edtJob = (EditText) findViewById(R.id.edt_job);
        edtBirthDate = (EditText) findViewById(R.id.edt_birthDay);
        rbMale = (RadioButton) findViewById(R.id.btn_male);
        rbFemale = (RadioButton) findViewById(R.id.btn_female);
        btnSave = (ImageButton) findViewById(R.id.btn_save);
        ava = (ImageView) findViewById(R.id.imgAva);
        txtEmail = (TextView) findViewById(R.id.text_email);
    }

    private void getUserData(String url, final int id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                if (object.getInt("volunteer_id") == id) {
                                    Log.d("ABC", String.valueOf(object.getInt("volunteer_id")));
                                    edtFName.setText(object.getString("volunteer_firstName"));
                                    edtLName.setText(object.getString("volunteer_lastName"));
                                    edtBirthDate.setText(object.getString("volunteer_birthDate"));
                                    edtJob.setText(object.getString("volunteer_job"));
                                    if (object.getString("volunteer_gender").equals("Male")) {
                                        rbMale.setChecked(true);
                                    } else
                                        rbFemale.setChecked(true);
                                    txtEmail.setText(object.getString("volunteer_email"));
                                }
                                Toast.makeText(ChangeProfile.this, "Load dữ liệu thành công", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_save: {
                upload(urlUpload);
                break;
            }
            case R.id.edt_birthDay: {
                chonNgay();
                break;
            }
        }
    }
    private void chonNgay(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Toast.makeText(ChangeProfile.this, simpleDateFormat.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                edtBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year,month,day);
        datePickerDialog.show();
    }

    private void upload(String url){
        final String gender = gd;
        final String fName = edtFName.getText().toString().trim();
        Log.d("ABC",gd);
        Log.d("BBB",fName);
        final String lName = edtLName.getText().toString().trim();
        final String job = edtJob.getText().toString().trim();
        final String birhtdate = edtBirthDate.getText().toString();
//        if (edtFName.getText().toString().isEmpty() || edtLName.getText().toString().isEmpty() || edtJob.getText().toString().isEmpty() || edtBirthDate.getText().toString().isEmpty()) {
//            Toast.makeText(getApplicationContext(), "You must type all inputs", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (NAME_PATTERN.matcher(fName).matches()) {
//            Toast.makeText(getApplicationContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (NAME_PATTERN.matcher(lName).matches()) {
//            Toast.makeText(getApplicationContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (NAME_PATTERN.matcher(job).matches()) {
//            Toast.makeText(getApplicationContext(), "Please enter a valid job", Toast.LENGTH_SHORT).show();
//            return;
//        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d("123","1");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("123","2");
                        if(response.trim() .equals("Success")){
                            Log.d("123","3");
                            Toast.makeText(ChangeProfile.this, "Update complete", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("123","4");
                    }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstName",fName);
                params.put("lastName",lName);
                params.put("gender",gender);
                params.put("birthDate",birhtdate);
                params.put("job",job);
                params.put("id", String.valueOf(id_Received));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}


//        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.names().get(0).equals("success")) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
//                    } else if (jsonObject.names().get(0).equals("error")) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> hashMap = new HashMap<String, String>();
//                hashMap.put("firstname", firstname.getText().toString());
//                hashMap.put("lastname", lastname.getText().toString());
//                hashMap.put("email", email.getText().toString().trim());
//                hashMap.put("password", password.getText().toString());
//
//                return hashMap;
//            }
//        };
//        requestQueue.add(request);
//
//    }
//});
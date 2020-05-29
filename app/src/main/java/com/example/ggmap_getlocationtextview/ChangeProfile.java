package com.example.ggmap_getlocationtextview;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Date;
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
    public Button btn_feedback;
    private TextView txtEmail;
    private ImageButton btnSave;
    private Dialog feedbackDialog;
    private String urlInsert = "http://192.168.43.54/androidwebservice/feedback.php";
    private String feedbackStatus="";
    private String feedbackContent="";
    private ImageView ava;
    private String urlGetData = "http://192.168.43.54/ub/getUser.php";
    private String urlUpload = "http://192.168.43.54/ub/updateProfile.php";
    private String idReceived;

    private static final Pattern NAME_PATTERN =
            Pattern.compile(
                    "[a-zA-Z]"
            );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("dat123","dat123");
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
        idReceived = intent.getStringExtra("userID");
        id_Received = Integer.parseInt(idReceived);
        getUserData(urlGetData, id_Received);

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedbackDialog = new Dialog(ChangeProfile.this);
                feedbackDialog.setCanceledOnTouchOutside(false);

            }
        });
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
        btn_feedback = (Button) findViewById(R.id.btn_dat);
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
                                    edtFName.setText(object.getString("volunteer_firstName"));
                                    edtLName.setText(object.getString("volunteer_lastName"));
                                    edtBirthDate.setText(object.getString("volunteer_birthDate"));
                                    edtJob.setText(object.getString("volunteer_job"));
                                    if (object.getString("volunteer_gender").equals("Male")) {
                                        rbMale.setChecked(true);
                                    } else
                                        rbFemale.setChecked(true);
                                    txtEmail.setText(object.getString("volunteer_email"));
                                    Toast.makeText(ChangeProfile.this, "Load dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                }
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
        final String lName = edtLName.getText().toString().trim();
        final String job = edtJob.getText().toString().trim();
        final String birhtdate = edtBirthDate.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim() .equals("Success")){
                            Toast.makeText(ChangeProfile.this, "Update complete", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
    public void Showfeedback(View v){
        feedbackDialog.setContentView(R.layout.feedback);

        final ImageButton ibtn_sad;
        final ImageButton ibtn_neutral;
        final ImageButton ibtn_happy;
        final EditText edt_fbContent;
        ImageButton btn_sendFeedback;
        TextView txt_close;

        //Anh Xa - feedback dialog
        txt_close = (TextView) feedbackDialog.findViewById(R.id.txt_close);
        ibtn_sad = (ImageButton) feedbackDialog.findViewById(R.id.ibtn_sad);
        ibtn_neutral = (ImageButton) feedbackDialog.findViewById(R.id.ibtn_normal);
        ibtn_happy = (ImageButton) feedbackDialog.findViewById(R.id.ibtn_happy);
        btn_sendFeedback = (ImageButton) feedbackDialog.findViewById(R.id.btn_sendFeedback);
        edt_fbContent = (EditText) feedbackDialog.findViewById(R.id.edt_feedbackContent);

        //event Đóng feedback
        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedbackDialog.dismiss();
            }
        });

        //set emotion from grey to yellow
        ibtn_sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibtn_sad.setImageResource(R.drawable.sad_red);
                ibtn_neutral.setImageResource(R.drawable.neutral_grey);
                ibtn_happy.setImageResource(R.drawable.happy_grey);
                feedbackStatus="sad";
            }
        });
        ibtn_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibtn_neutral.setImageResource(R.drawable.neutral_yellow);
                ibtn_sad.setImageResource(R.drawable.sad_grey);
                ibtn_happy.setImageResource(R.drawable.happy_grey);
                feedbackStatus="neutral";
            }
        });
        ibtn_happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibtn_happy.setImageResource(R.drawable.happy_blue);
                ibtn_sad.setImageResource(R.drawable.sad_grey);
                ibtn_neutral.setImageResource(R.drawable.neutral_grey);
                feedbackStatus="happy";
            }
        });

        //gọi phương thức send data feedback lên phpMyAdmin
        btn_sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedbackStatus.isEmpty() && feedbackContent.isEmpty()){
                    Toast.makeText(ChangeProfile.this,"Please input or choose",Toast.LENGTH_SHORT).show();
                }else{
                    feedbackContent = edt_fbContent.getText().toString().trim();
                    insertFeedback(urlInsert);
                }
            }
        });

        feedbackDialog.show();
    }

    //method insert feedback to server phpMyAdmin
    private void insertFeedback(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success")){
                    Toast.makeText(ChangeProfile.this,"Success",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ChangeProfile.this,"Fail",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Date today=new Date(System.currentTimeMillis());
                SimpleDateFormat timeFormat= new SimpleDateFormat("dd/MM/yyyy");
                String date = timeFormat.format(today.getTime());

                Map<String,String> params = new HashMap<>();
                params.put("feedback_status",feedbackStatus);
                params.put("feedback_content",feedbackContent);
                params.put("user_ID",idReceived);
                params.put("feedback_date",date);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}


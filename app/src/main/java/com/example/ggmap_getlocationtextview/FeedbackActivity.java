package com.example.ggmap_getlocationtextview;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class FeedbackActivity extends AppCompatActivity {
    private Dialog feedbackDialog;
    private TextView txt_userID;
    private EditText edt_email;
    Button btnOK;
    private String urlGetUserID ="http://192.168.43.54/androidwebservice/login.php";
    private String urlInsert = "http://192.168.43.54/androidwebservice/feedback.php";
    private String feedbackStatus="";
    private String feedbackContent="";
    String user_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_main);
        feedbackDialog = new Dialog(this);
        feedbackDialog.setCanceledOnTouchOutside(false);
    }

    //method lấy userID của người gửi feedback từ email của người đó
    private void getUserID(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String userID = object.getString("user_ID");
                        String email = object.getString("email");
                        if(email.equals(edt_email.getText().toString())){
                            user_ID = userID;
                            txt_userID.setText(userID);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FeedbackActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }


    //ShowFeedBack Dialog
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
                    Toast.makeText(FeedbackActivity.this,"Please input or choose",Toast.LENGTH_SHORT).show();
                }else{
                    getUserID(urlGetUserID);
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
                    Toast.makeText(FeedbackActivity.this,"Success",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FeedbackActivity.this,"Fail",Toast.LENGTH_SHORT).show();
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
                params.put("user_ID",txt_userID.getText().toString());
                params.put("feedback_date",date);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

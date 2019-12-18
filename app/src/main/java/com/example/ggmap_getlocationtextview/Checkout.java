package com.example.ggmap_getlocationtextview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Checkout extends AppCompatActivity implements View.OnClickListener{
    private int wasteId_Received;
    private int volunteerId_Received;
    private String pPeople = "0";
    private String pMaterial = "0";
    private String pSize = "0";
    private TextView txt_material;
    private TextView txt_size;
    private TextView txt_people;
    private CheckBox cb_material;
    private CheckBox cb_size;
    private CheckBox cb_people;
    private Button btn_checkout;
    private ImageView imgeCheckout;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap FixBitmap;
    String TextImageTag = "image_tag";
    String TextImageName = "image_dat";
    String TextProcess = "process";
    String TextCheckin_id = "checkin_id";
    String TextSize = "size";
    String TextPeople = "people";
    String TextMaterial = "material";

    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String ImageCheckout = "";
    String userId = "";
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    private SeekBar sk_process;
    private TextView txt_process;
    String urlCheckout = "http://192.168.43.112/ub/checkout.php";
    String urlGetCheckinId = "http://192.168.43.112/ub/getCheckinId.php";
    String urlGetWasteData= "http://192.168.43.112/ub/getWaste.php";
    String urlUpdateScore = "http://192.168.43.112/ub/score.php";
    private String  checkinID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkout);
        Intent intent = getIntent();
        volunteerId_Received = Integer.parseInt(intent.getStringExtra("userID"));
        wasteId_Received = Integer.parseInt(intent.getStringExtra("wasteID"));
        Log.e("Hien!",volunteerId_Received+"");
        Log.e("Hien!",wasteId_Received+"");
        getCheckinID(urlGetCheckinId,wasteId_Received,volunteerId_Received);
        getWasteData(urlGetWasteData, wasteId_Received);
        byteArrayOutputStream = new ByteArrayOutputStream();
        anhXa();
        txt_process.setText(String.valueOf(sk_process.getProgress())+"%");
        sk_process.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int process = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                process = progress;
                txt_process.setText(String.valueOf(process)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_checkout.setOnClickListener(this);
        imgeCheckout.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(Checkout.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
            }
        }
    }

    public void anhXa() {
        btn_checkout = (Button) findViewById(R.id.btn_checkout);
        imgeCheckout = (ImageView) findViewById(R.id.imgeCheckout);
        txt_material = (TextView) findViewById(R.id.txtMaterial);
        txt_size = (TextView) findViewById(R.id.txtSize);
        txt_people = (TextView) findViewById(R.id.txtPeople);
        cb_material = (CheckBox) findViewById(R.id.tb_material);
        cb_size = (CheckBox) findViewById(R.id.tb_size);
        cb_people = (CheckBox) findViewById(R.id.tb_people);
        sk_process = findViewById(R.id.sbProcess);
        txt_process = findViewById(R.id.txt_process);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imgeCheckout.setImageBitmap(FixBitmap);
                    btn_checkout.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Checkout.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            imgeCheckout.setImageBitmap(FixBitmap);
            btn_checkout.setVisibility(View.VISIBLE);
//              saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UploadImageToServer(final String checkinId, final String txtProcess, final String nameImage) {
        if(cb_material.isChecked()){
            pMaterial = "1";
        }
        if(cb_people.isChecked()){
            pPeople = "1";
        }
        if(cb_size.isChecked()){
            pSize = "1";
        }
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT).toString();

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                Toast.makeText(Checkout.this, string1, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {

                Checkout.ImageProcessClass imageProcessClass = new Checkout.ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(TextCheckin_id, checkinId);
                HashMapParams.put(TextImageName, ConvertImage);
                HashMapParams.put(TextImageTag, nameImage);
                HashMapParams.put(TextProcess, txtProcess);
                HashMapParams.put(TextSize, pSize);
                HashMapParams.put(TextPeople, pPeople);
                HashMapParams.put(TextMaterial, pMaterial);
                HashMapParams.put("waste_id",String.valueOf(wasteId_Received));
                String FinalData = imageProcessClass.ImageHttpRequest(urlCheckout, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkout: {
                String getProcess1 = txt_process.getText()+"";
                String getProcess2 = getProcess1.substring(0,getProcess1.length()-1);
                UploadImageToServer(checkinID,getProcess2,  "ckImage"+checkinID);
                uploadScore(urlUpdateScore,volunteerId_Received);
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                finish();
                Intent intent1 = new Intent(this, MapsActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.imgeCheckout: {
                showPictureDialog();

                break;
            }
        }
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilder.toString();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(Checkout.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getWasteData(String link, final int waste_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, link, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                if (object.getInt("waste_id") == waste_id) {
                                        txt_material.setText(object.getString("waste_material"));
                                        txt_size.setText(String.valueOf(object.getString("waste_size")));
                                        txt_people.setText(String.valueOf(object.getInt("waste_people")));
                                }
                                Toast.makeText(Checkout.this, "Load dữ liệu thành công", Toast.LENGTH_SHORT).show();
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

    private void getCheckinID(String link, final int waste_id, final int volunteer_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, link, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                if (object.getInt("waste_id") == waste_id && object.getInt("volunteer_id") == volunteer_id) {
                                    checkinID = String.valueOf(object.getInt("checkin_id"));
                                    Log.d("AAAA",checkinID+" ");
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

    private void uploadScore(String url, final int volunteerId){
        String rank = (String) txt_size.getText();
        String scoreRank = "0";
        if(rank.equals("Small"))
            scoreRank = "5";
        else if(rank.equals("Medium"))
            scoreRank = "10";
        else
            scoreRank ="20";
        Log.d("AAB",scoreRank);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String finalScoreRank = scoreRank;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("volunteer_id",String.valueOf(volunteerId));
                params.put("score", finalScoreRank);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}


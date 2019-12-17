package com.example.ggmap_getlocationtextview;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity
//       implements View.OnClickListener
{
    private static String JSON_STRING;
    private static final String UPLOAD_URL = "http://192.168.1.6/upload/insert_image.php";
    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView;
    private String size;
    private EditText etCaption;
    private TextView tvPath, tvIdmax;
    private ImageButton btnReport;
    private Bitmap bitmap;
    private Uri filePath;
    private RadioButton radioButton_Small, radioButton_Medium, radioButton_Large;
    private EditText etMaterial;
    private EditText etNumberOfPeople;
    private double latitude, longtitude;
    private String addressWaste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        reflect();
        requestStoragePermission();

        imageView.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent();
                                             intent.setType("image/*");
                                             intent.setAction(Intent.ACTION_GET_CONTENT);
                                             startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_REQUEST_CODE);
                                             sendFCMPush();
                                         }
                                     }
        );

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFCMPush();
                uploadMultipart();

            }
        });

    }

    private void reflect() {
        //id = tvIdmax.getText().toString();
        imageView = (ImageView) findViewById(R.id.image);
        etCaption = (EditText) findViewById(R.id.etCaption);
        etMaterial = findViewById(R.id.etMaterial);
        etNumberOfPeople = findViewById(R.id.etNumberOfPeople);
        tvPath = (TextView) findViewById(R.id.path);
        radioButton_Small = findViewById(R.id.radioButton_Small);
        radioButton_Medium = findViewById(R.id.radioButton_Medium);
        radioButton_Large = findViewById(R.id.radioButton_Large);
        btnReport = findViewById((R.id.btnReport));
        tvIdmax = findViewById(R.id.idmax);
    }

    //    @Override
//    public void onClick(View view) {
//        sendFCMPush();
//        if (view == imageView) {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_REQUEST_CODE);
//        } else if (view == btnReport) {
//            uploadMultipart();
//
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                tvPath.setText("Path: ".concat(getPath(filePath)));
                imageView.setImageBitmap(bitmap);
                //XU LY TAI DAY
                uploadMultipart1();
                Toast.makeText(this, "Loading recommend please wait...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadMultipart1() {
        String url = "http://192.168.1.6/upload/insert_image1.php";

        String caption = etCaption.getText().toString().trim();
        //String size=etSize.getText().toString().trim();
        //getting the actual path of the image
        String path = getPath(filePath);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, url)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("caption", caption) //Adding text parameter to the request
                    .addParameter("wasteLocation_longtitude", String.valueOf(longtitude))
                    .addParameter("wasteLocation_latitude", String.valueOf(latitude))
                    .addParameter("wasteLocation_address", String.valueOf(addressWaste))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            //Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadMultipart() {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");

        if (radioButton_Small.isChecked()) {
            size = "Small";
        }
        if (radioButton_Medium.isChecked()) {
            size = "Medium";
        }
        if (radioButton_Large.isChecked()) {
            size = "Big";
        }
        String caption = etCaption.getText().toString().trim();
        //String size=etSize.getText().toString().trim();
        String material = etMaterial.getText().toString().trim();
        String people = etNumberOfPeople.getText().toString().trim();
        //getting the actual path of the image
        String path = getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("caption", caption) //Adding text parameter to the request
                    .addParameter("size", size)
                    .addParameter("material", material)
                    .addParameter("people", people)
                    .addParameter("wasteLocation_longtitude", String.valueOf(longtitude))
                    .addParameter("wasteLocation_latitude", String.valueOf(latitude))
                    .addParameter("wasteLocation_address", String.valueOf(addressWaste))
                    .addParameter("waste_date", String.valueOf(date))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Intent intentBackMap = new Intent(ReportActivity.this, MapsActivity.class);
            startActivity(intentBackMap);
            finish();
            startActivity(intentBackMap);
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private UploadServiceBroadcastReceiver receiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
            super.onCompleted(uploadId, serverResponseCode, serverResponseBody);
            getPeople();
            getSize();
            getMaterial();
            //Intent intentBackMap=new Intent(ReportActivity.this, MapsActivity.class);
            //startActivity(intentBackMap);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.unregister(this);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void getPeople() {
        new docSoNguoi().execute();
    }

    public void getMaterial() {
        new docChatLieu().execute();
    }

    public void getSize() {
        new docSize().execute();
    }


    public class docSoNguoi extends AsyncTask<Void, Void, String> {

        String url;

        @Override
        protected void onPreExecute() {
            url = "http://192.168.1.6/upload/getPeople.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            etNumberOfPeople.setText(result);
        }
    }

    public class docChatLieu extends AsyncTask<Void, Void, String> {

        String url;

        @Override
        protected void onPreExecute() {
            url = "http://192.168.1.6/upload/getMaterial.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            etMaterial.setText(result);
        }
    }

    public class docSize extends AsyncTask<Void, Void, String> {

        String url;

        @Override
        protected void onPreExecute() {
            url = "http://192.168.1.6/upload/getSize.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Small")) {
                radioButton_Small.setChecked(true);
            }
            if (result.equals("Medium")) {
                radioButton_Medium.setChecked(true);
            }
            if (result.equals("Big")) {
                radioButton_Large.setChecked(true);
            }
        }
    }

    public static class SingleUploadBroadcastReceiver extends UploadServiceBroadcastReceiver {

        public interface Delegate {
            void onProgress(int progress);

            void onProgress(long uploadedBytes, long totalBytes);

            void onError(Exception exception);

            void onCompleted(int serverResponseCode, byte[] serverResponseBody);

            void onCancelled();
        }

        private String mUploadID;
        private Delegate mDelegate;

        public void setUploadID(String uploadID) {
            mUploadID = uploadID;
        }

        public void setDelegate(Delegate delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onProgress(String uploadId, int progress) {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onProgress(progress);
            }
        }

        @Override
        public void onProgress(String uploadId, long uploadedBytes, long totalBytes) {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onProgress(uploadedBytes, totalBytes);
            }
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onError(exception);
            }
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onCompleted(serverResponseCode, serverResponseBody);
            }
        }

        @Override
        public void onCancelled(String uploadId) {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onCancelled();
            }
        }
    }

    private void sendFCMPush() {

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");
        MapsActivity m = new MapsActivity();
        final String SERVER_KEY = "AAAA5X8ZDBE:APA91bHDkSbJW0In5hLU_8mOwP9zhNuD_E3WzYi8-0W0UT_rAdIPy3HrmaxNlP__KjmipMjaaJ08AqQeB591ynOeMEKj2k31e-bm1y1jFUq_HvhonynWJkJVEjoR6DojXts2MTtM_AQB";
        Log.e("cccccc", String.valueOf(addressWaste));
        String title = "Uber";
        String msg = "Have new waste near " + String.valueOf(addressWaste);
        String token = "epcG9vI67-E:APA91bFZH6i48Tm_6i2Ykf30JmHFjP0_rcv2Emqyc0ekk8GXTV4LtSc8DgxsjMrPJCJLnBqQBqRbGEzY7CuNYmIUwVgS1A0uTRUJgFRp_3O3-mYpYy4L4LaVGQi1XTVv1leadOuhEFJc";

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            //objData.put("sound", "default");
            //objData.put("icon", "icon_name"); //   icon_name
            //objData.put("tag", "/topics/allDevices");
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);

            obj.put("to", "/topics/allDevices");
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("return here>>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("True", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("False", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
    }

}


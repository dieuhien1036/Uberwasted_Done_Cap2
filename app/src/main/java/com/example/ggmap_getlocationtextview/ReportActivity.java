package com.example.ggmap_getlocationtextview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class ReportActivity extends AppCompatActivity
{
    private final String cardboard_big = "cardboard_big_biod";
    private final String cardboard_small = "cardboard_small_biod";
    private final String metal_big = "metal_big_nonbiod";
    private final String metal_small = "metal_small_nonbiod";
    private final String plastic_big = "plastic_big_nonbiod";
    private final String plastic_small = "plastic_small_nonbiod";
    private final String glass_big = "glass_big_nonbiod";
    private final String glass_small = "glass_small_nonbiod";
    private final String big_size = "big";
    private final String small_size = "small";
    private final String biod_type = "biod";
    private final String noBiod_type = "non biod";


    private static String JSON_STRING;
    private static final String UPLOAD_URL = "http://192.168.1.4/upload/upload_to_server.php";
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView;
    private String size = "small";
    private String biod = "biod";
    private String folder = "cardboard_big_biod";
    private TextView tvPath, tvIdmax;
    private ImageButton btnReport;
    private Bitmap bitmap;
    private Uri filePath;
    private RadioButton radioButton_Small, radioButton_Large, radioButton_biod, radioButton_nobiod;
    private EditText etMaterial;
    private double latitude, longtitude;
    private String addressWaste;

    private int GALLERY = 1, CAMERA = 2;
    Bitmap FixBitmap;
    String ImageTag = "image_tag";
    String ImageDat = "image_dat";
    String JoinID = "join_id";
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    String urlUploadForCompare = "http://192.168.1.4/upload/uploadForCompare.php";
    String urlUploadToServer = "http://192.168.1.4/upload/uploadToServer.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        reflect();
        requestStoragePermission();

        imageView.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             showPictureDialog();
                                         }
                                     }
        );
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageToServer();
            }
        });
        if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
            }
        }
    }

    private void reflect() {
        imageView = findViewById(R.id.image);
        etMaterial = findViewById(R.id.etMaterial);
        tvPath = findViewById(R.id.path);
        radioButton_Small = findViewById(R.id.radioButton_Small);
        radioButton_Large = findViewById(R.id.radioButton_Large);
        radioButton_biod = findViewById(R.id.radioButton_biod);
        radioButton_nobiod = findViewById(R.id.radioButton_nonbiod);
        btnReport = findViewById((R.id.btnReport));
        tvIdmax = findViewById(R.id.idmax);
    }


    public void updateImageWasteForClassification() {
        String url = "http://192.168.1.4/upload/upload_for_compare.php";
        String caption = " ";

        //getting the actual path of the image
        String path = getPath(filePath);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part requestẽ
            new MultipartUploadRequest(this, uploadId, url)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("size", size) //Adding text parameter to the request
                    .addParameter("wasteLocation_longtitude", String.valueOf(longtitude))
                    .addParameter("wasteLocation_latitude", String.valueOf(latitude))
                    .addParameter("wasteLocation_address", String.valueOf(addressWaste))
//                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
        }
    }


    public void uploadImageToServer() {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");

        if (radioButton_Small.isChecked()) {
            size = small_size;
        }
        if (radioButton_Large.isChecked()) {
            size = big_size;
        }

        if (radioButton_biod.isChecked()) {
            biod = biod_type;
        }
        if (radioButton_nobiod.isChecked()) {
            biod = noBiod_type;
        }
        String material = etMaterial.getText().toString().trim();

        switch (size) {
            case big_size: {
                switch (material) {
                    case "cardboard": {
                        folder = cardboard_big;
                        break;
                    }
                    case "metal": {
                        folder = metal_big;
                        break;
                    }
                    case "glass": {
                        folder = glass_big;
                        break;
                    }
                    case "plastic": {
                        folder = plastic_big;
                    }
                }
                break;
            }
            case small_size: {
                switch (material) {
                    case "cardboard": {
                        folder = cardboard_small;
                        break;
                    }
                    case "metal": {
                        folder = metal_small;
                        break;
                    }
                    case "glass": {
                        folder = glass_small;
                        break;
                    }
                    case "plastic": {
                        folder = plastic_small;
                    }
                }
                break;
            }
            default: {
            }
        }

        //getting the actual path of the image
        String path = getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("file_folder", folder)
                    .addParameter("size", size)
                    .addParameter("material", material)
                    .addParameter("biod", biod)
                    .addParameter("wasteLocation_longtitude", String.valueOf(longtitude))
                    .addParameter("wasteLocation_latitude", String.valueOf(latitude))
                    .addParameter("wasteLocation_address", String.valueOf(addressWaste))
                    .addParameter("waste_date", String.valueOf(date))
//                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private UploadServiceBroadcastReceiver receiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
            super.onCompleted(uploadId, serverResponseCode, serverResponseBody);
            getResult();
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

    public void getResult() {
        new readWaste().execute();
    }

    public class readWaste extends AsyncTask<Void, Void, String> {
        String url;

        @Override
        protected void onPreExecute() {
            url = "http://192.168.1.4/upload/getMaterial.php";
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
            String[] wasteResult = result.split("_");
            etMaterial.setText(wasteResult[0]);
            if (wasteResult[1].equals("big")) {
                radioButton_Large.setChecked(true);
            } else {
                radioButton_Small.setChecked(true);
            }
            if (wasteResult[2].contains("nonbiod")) {
                radioButton_nobiod.setChecked(true);
            } else {
                radioButton_biod.setChecked(true);
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
        byteArrayOutputStream = new ByteArrayOutputStream();
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    imageView.setImageBitmap(FixBitmap);
                    btnReport.setVisibility(View.VISIBLE);
                    UploadImageForCompare("imageCompare");

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ReportActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(FixBitmap);
            btnReport.setVisibility(View.VISIBLE);
            UploadImageForCompare("imageCompare");
        }
    }

    public void UploadImageForCompare(final String nameImage) {

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                Log.d(ReportActivity.class.getName(), "Upload Success");
                getResult();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageTag, nameImage);
                HashMapParams.put(ImageDat, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(urlUploadForCompare, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public void UploadImageToServer() {
        long millis = System.currentTimeMillis();
        final java.sql.Date date = new java.sql.Date(millis);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("wasteLocation_latitude", 0.);
        longtitude = intent.getDoubleExtra("wasteLocation_longtitude", 0.);
        addressWaste = intent.getStringExtra("wasteLocation_address");

        if (radioButton_Small.isChecked()) {
            size = small_size;
        }
        if (radioButton_Large.isChecked()) {
            size = big_size;
        }

        if (radioButton_biod.isChecked()) {
            biod = biod_type;
        }
        if (radioButton_nobiod.isChecked()) {
            biod = noBiod_type;
        }
        final String material = etMaterial.getText().toString().trim();

        switch (size) {
            case big_size: {
                switch (material) {
                    case "cardboard": {
                        folder = cardboard_big;
                        break;
                    }
                    case "metal": {
                        folder = metal_big;
                        break;
                    }
                    case "glass": {
                        folder = glass_big;
                        break;
                    }
                    case "plastic": {
                        folder = plastic_big;
                    }
                }
                break;
            }
            case small_size: {
                switch (material) {
                    case "cardboard": {
                        folder = cardboard_small;
                        break;
                    }
                    case "metal": {
                        folder = metal_small;
                        break;
                    }
                    case "glass": {
                        folder = glass_small;
                        break;
                    }
                    case "plastic": {
                        folder = plastic_small;
                    }
                }
                break;
            }
            default: {
            }
        }
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        final String imageNameToServer = dateFormatter.format(today);

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                Intent intentBackMap = new Intent(ReportActivity.this, MapsActivity.class);
                startActivity(intentBackMap);
                finish();
                startActivity(intentBackMap);
                Toast.makeText(ReportActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageTag, imageNameToServer);
                HashMapParams.put(ImageDat, ConvertImage);
                HashMapParams.put("file_folder", folder);
                HashMapParams.put("size", size);
                HashMapParams.put("material", material);
                HashMapParams.put("biod", biod);
                HashMapParams.put("wasteLocation_longtitude", String.valueOf(longtitude));
                HashMapParams.put("wasteLocation_latitude", String.valueOf(latitude));
                HashMapParams.put("wasteLocation_address", String.valueOf(addressWaste));
                HashMapParams.put("waste_date", String.valueOf(date));
                String FinalData = imageProcessClass.ImageHttpRequest(urlUploadToServer, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
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
                Toast.makeText(ReportActivity.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();
            }
        }
    }
}

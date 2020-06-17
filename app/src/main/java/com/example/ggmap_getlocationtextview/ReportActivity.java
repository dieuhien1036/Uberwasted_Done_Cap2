package com.example.ggmap_getlocationtextview;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import javax.net.ssl.HttpsURLConnection;

public class ReportActivity extends AppCompatActivity {
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}

package com.example.ggmap_getlocationtextview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, joinDialog.BottomSheetListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //location
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int locationRequestCode = 1;
    private double currentLatitude = 0.0;
    private double currentLongtitude = 0.0;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_REQUEST = 7172;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FASTEST_INTERVEL = 10000;

    private static int DISTANCE = 10;

    private joinDialog.BottomSheetListener mListener;

    private Integer INTENT_ACTIVITY_FOR_RESULT = 1;

    Marker markerSearch;
    Marker markerWaste;
    List<Marker> markerJoin = new ArrayList<>();
    List<Marker> markerWasteList = new ArrayList<>();
    private List<Polyline> polyLinePaths;

    SearchView searchViewLocation;
    ImageButton btn_location;
    ImageButton ibtn_clean;
    ImageButton ibtn_report;
    ImageButton ibtn_rank;
    ImageButton ibtn_account;
    TextView txt_distance;
    TextView txt_duration;
    String username = null;
    String dateOfBirth = null;
    String userJob = null;
    String userGender = null;
    String userID = null;
    String userScore = null;
    String url = "http://10.10.51.193/androidwebservice/wasteLocation.php";
    String getWasteJoinURL = "http://10.10.51.193/androidwebservice/WasteJoin.php";
    String wasteID = null;

    //Firebase
    public DatabaseReference currentUserRef, locations;
    public String currentUserToken;

    //Notification
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
            "key=" + "AAAAb-lvNCk:APA91bHm1qhc2NdbsXHnSNAd9F99NeDZ0oCUAM94m4ON_gtnucW13fhtWVuzHehmkEr-5CRkR2-2awBwFYWR3ZUjGkoziIFJ5RgQm5hdN4j7HQpuCLKX18Fp8U6IoJhoau5_rzDTNfqs";
    private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    List<String> TOPIC = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent mapActivityIntent = getIntent();
        username = mapActivityIntent.getStringExtra("username");
        dateOfBirth = mapActivityIntent.getStringExtra("dateOfBirth");
        userJob = mapActivityIntent.getStringExtra("userJob");
        userGender = mapActivityIntent.getStringExtra("userGender");
        userID = mapActivityIntent.getStringExtra("userID");
        userScore = mapActivityIntent.getStringExtra("userScore");

        checkPermisson();
        getLocation(url);
        getDataJoinID(getWasteJoinURL);
        reflect();
        searchLocation();
        reportWaste();
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location();
            }
        });
        account();
        ranking();
        ibtn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, CleanActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("ABC", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        currentUserToken = task.getResult().getToken();
                        Log.d("ABC123", currentUserToken);
                    }
                });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
        setUpFirebase();
    }

    private void ranking() {
        ibtn_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, Ranking.class);
                startActivity(intent);
            }
        });
    }

    private void getDataJoinID(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        double wasteJoinLat = object.getDouble("waste_latitude");
                        double wasteJoinLong = object.getDouble("waste_longtitude");
                        String volunteer_id = object.getString("volunteer_id");
                        if (volunteer_id.equals(userID)) {
                            for (int j = 0; j < markerWasteList.size(); j++) {
                                if (markerWasteList.get(j).getPosition().latitude == wasteJoinLat
                                        && markerWasteList.get(j).getPosition().longitude == wasteJoinLong) {
                                    markerWasteList.get(j).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                    markerJoin.add(markerWasteList.get(j));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @SuppressLint("MissingPermission")
    private void location() {
        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //Set blue point  == true
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Set location at LatLng ( user's current location)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in street
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private void account() {
        ibtn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ChangeProfile.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    private void reportWaste() {
        ibtn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(currentLatitude, currentLongtitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String addressWaste = addressList.get(0).getAddressLine(0);


                Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
                intent.putExtra("wasteLocation_latitude", currentLatitude);
                intent.putExtra("wasteLocation_longtitude", currentLongtitude);
                intent.putExtra("wasteLocation_address", addressWaste);
                startActivityForResult(intent, INTENT_ACTIVITY_FOR_RESULT);

            }
        });
    }

    //check user's permisson ( FINE_LOCATION ; COARSE_LOCATION )
    private void checkPermisson() {
        //If user don't have this this permisson
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Call onRequestPermissionsResult to request user's permisson
            ActivityCompat.requestPermissions(this,
                    //Create a String array include 2 permission (FINE_LOCATION ; COARSE_LOCATION)
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}
                    , locationRequestCode);
        } else {
            //If user granted use MAP
            //Get current location
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongtitude = location.getLongitude();
                        //Call onReadyMap()
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermisson();
                }
            }
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            return;
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        //
        mMap.setMyLocationEnabled(true);
        //Set blue point  == true
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Set location at LatLng ( user's current location)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in street
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        //
        joinDialog(url);

    }

    //get waste Marker from database into MAP
    private void getLocation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        double wasteLocation_latitude = Double.parseDouble(object.getString("waste_latitude"));
                        double wasteLocation_longtitude = Double.parseDouble(object.getString("waste_longtitude"));
                        LatLng latLng = new LatLng(wasteLocation_latitude, wasteLocation_longtitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(wasteLocation_latitude, wasteLocation_longtitude, 1);
                            String str = addressList.get(0).getAddressLine(0);
                            markerWaste = mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                            markerWasteList.add(markerWaste);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void reflect() {
        searchViewLocation = (SearchView) findViewById(R.id.svLocation);
        btn_location = (ImageButton) findViewById(R.id.btn_location);
        ibtn_report = (ImageButton) findViewById(R.id.ibtn_report);
        ibtn_clean = (ImageButton) findViewById(R.id.ibtn_clean);
        ibtn_rank = (ImageButton) findViewById(R.id.ibtn_rank);
        ibtn_account = (ImageButton) findViewById(R.id.ibtn_account);
        txt_distance = (TextView) findViewById(R.id.txt_distance);
        txt_duration = (TextView) findViewById(R.id.txt_duration);
    }

    private void searchLocation() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchViewLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    //delete the old marker
                    if (markerSearch != null) { //nếu không kiểm tra đk này thì lúc đầu chưa có sẽ xóa luôn marker => k có marker này để mark nữa
                        markerSearch.remove();
                    }
                    //
                    String location = searchViewLocation.getQuery().toString();
                    List<Address> addressList = null;
                    if (location != null || !location.equals("")) {
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (Exception e) {
                        }
                        if (addressList.size() > 0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            //getThroughfare : get street name
                            markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        } else {
                            List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongtitude, 1);
                            String str = addresses.get(0).getLocality();
                            str = str + " " + addresses.get(0).getAdminArea();
                            str = str + " " + addresses.get(0).getCountryName();

                            location = location.concat(",");
                            location = location + " " + str;

                            addressList = geocoder.getFromLocationName(location, 1);
                            Address address = addressList.get(0);

                            if (address.getThoroughfare() != null) {
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            } else {
                                Toast.makeText(MapsActivity.this, "Don't have this address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(MapsActivity.this); //when you already implement OnMapReadyCallback in your fragment
    }

    private void joinDialog(final String url) {
        //set event for marker to display join dialog
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker m) {

                //marker search can't join
                if (markerSearch != null) {
                    if ((m.getPosition().latitude == markerSearch.getPosition().latitude)
                            && (m.getPosition().longitude == markerSearch.getPosition().longitude)) {
                        return false;
                    }
                }
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(m.getPosition().latitude, m.getPosition().longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String waste_address = addressList.get(0).getAddressLine(0);
                final Bundle bundle = new Bundle();

                bundle.putString("waste_address", waste_address);
                bundle.putDouble("currentLatitude", currentLatitude);
                bundle.putDouble("currentLongtitude", currentLongtitude);
                bundle.putDouble("wasteLatitude", m.getPosition().latitude);
                bundle.putDouble("wasteLongtitude", m.getPosition().longitude);
                bundle.putString("username", username);
                bundle.putString("dateOfBirth", dateOfBirth);
                bundle.putString("userJob", userJob);
                bundle.putString("userGender", userGender);
                bundle.putString("userID", userID);
                bundle.putString("userScore", userScore);
                Log.e("CheckABC", userID + "-"
                        + dateOfBirth + "-" + userJob + "-" + userGender + "-" + userScore);
                RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                double wasteLocation_latitude = Double.parseDouble(object.getString("waste_latitude"));
                                double wasteLocation_longtitude = Double.parseDouble(object.getString("waste_longtitude"));
                                if (wasteLocation_latitude == m.getPosition().latitude && wasteLocation_longtitude == m.getPosition().longitude) {
                                    String waste_people = object.getString("waste_biod");
                                    String waste_size = object.getString("waste_size");
                                    String waste_image = object.getString("waste_image");
                                    bundle.putString("waste_people", waste_people);
                                    bundle.putString("waste_size", waste_size);
                                    bundle.putString("waste_image", waste_image);
                                    joinDialog dialog = new joinDialog();
                                    dialog.setArguments(bundle);
                                    dialog.show(getSupportFragmentManager(), "example");
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
                        Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonArrayRequest);
                return true;
            }
        });
    }

    @Override
    public void onDirectionFinderStart() {
        if (polyLinePaths != null) {
            for (Polyline polyLine : polyLinePaths) {
                polyLine.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        onDirectionFinderStart();
        polyLinePaths = new ArrayList<>();
        for (Route route : routes) {
            txt_duration.setText(route.duration + " m");
            txt_distance.setText(route.distance + " km");
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.RED).width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }
            polyLinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public void changeColorJoinMaker(double wasteJoinLat, double wasteJoinLon, String wasteID, String wasteAddress) {
        for (int i = 0; i < markerWasteList.size(); i++) {
            if (markerWasteList.get(i).getPosition().latitude == wasteJoinLat && markerWasteList.get(i).getPosition().longitude == wasteJoinLon) {
                markerWasteList.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                if (wasteID != null) {
                    markerJoin.add(markerWasteList.get(i));
                    this.wasteID = wasteID;
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean result = data.getBooleanExtra("result", false);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVEL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            locations = FirebaseDatabase.getInstance().getReference("locations");
            currentUserRef = FirebaseDatabase.getInstance().getReference("locations");
            currentUserRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new TrackingLocation(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    currentUserToken,
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()
            ));
        } else {
            Toast.makeText(this, "Couldn't get the location", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private void setUpFirebase() {
        locations = FirebaseDatabase.getInstance().getReference("locations");
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TOPIC.clear();
                for (DataSnapshot location : dataSnapshot.getChildren()) {
                    TrackingLocation tracking = location.getValue(TrackingLocation.class);
                    LatLng volunteerLocation = new LatLng(tracking.getLat(), tracking.getLng());

                    Location currentUser = new Location("");
                    currentUser.setLatitude(currentLatitude);
                    currentUser.setLongitude(currentLongtitude);

                    Location volunteer = new Location("");
                    currentUser.setLatitude(tracking.getLat());
                    currentUser.setLongitude(tracking.getLng());

                    if (distance(currentUser, volunteer) < 3000.0) {
                        if (TOPIC.add(tracking.getToken()))
                            mMap.addMarker(new MarkerOptions()
                                    .position(volunteerLocation)
                                    .title(tracking.getEmail())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongtitude), 12.0f));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                locations.onDisconnect().removeValue();
            }
        });
    }

    private double distance(Location user, Location volunteer) {
        double theta = user.getLatitude() - volunteer.getLatitude();
        double dist = Math.sin(deg2rad(user.getLatitude()))
                * Math.sin(deg2rad(volunteer.getLatitude()))
                * Math.cos(deg2rad(user.getLongitude()))
                * Math.cos(deg2rad(volunteer.getLongitude()))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg((dist));
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double rad2deg(double dist) {
        return (dist * 180 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private void pushNofication() {
        TOPIC.add("cKxwLkW2vis:APA91bEYAHtVxEeUr53ZBBSMQdFKuQCYRJa0JqPquQXJu" +
                "nHZIQG7U4UHHAhco_mX4Hc27WxRRulyRXYunKlSbljN7-0u0JPoHmxOSv-ZdRSr470F22YAdxXGZZvEzBc3NcwuhiVSO6TA");
        NOTIFICATION_TITLE = "UberWaste";
        NOTIFICATION_MESSAGE = "Have new a waste near you";
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("registration_ids", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendFCM(notification);
    }

    private void sendFCM(JSONObject notification) {
        Log.e("TAG:D", "send notification");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API,
                notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MapsActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}

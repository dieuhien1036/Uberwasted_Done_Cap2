package com.example.ggmap_getlocationtextview;

import android.Manifest;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, joinDialog.BottomSheetListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int locationRequestCode = 1;
    private double currentLatitude = 0.0;
    private double currentLongtitude = 0.0;

    private joinDialog.BottomSheetListener mListener;

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
    String userGender  = null;
    String userID = null;
    String userScore = null;
    String url = "http://192.168.43.54/androidwebservice/wasteLocation.php";
    String getWasteJoinURL ="http://192.168.43.54/androidwebservice/WasteJoin.php";
    String wasteID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
                Intent intent = new Intent(MapsActivity.this,CleanActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });
    }

    private void ranking(){
        ibtn_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,Ranking.class);
                startActivity(intent);
            }
        });
    }
    private void getDataJoinID(String url){
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
                        if(volunteer_id.equals(userID)) {
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

    private void location(){
        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);
        mMap.setMyLocationEnabled(true);
        //Set blue point  == true
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Set location at LatLng ( user's current location)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in street
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }

    private void account(){
        ibtn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,ChangeProfile.class);
                intent.putExtra("userID",userID);
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
                intent.putExtra("wasteLocation_latitude",currentLatitude);
                intent.putExtra("wasteLocation_longtitude",currentLongtitude);
                intent.putExtra("wasteLocation_address",addressWaste);

                startActivity(intent);

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        //
        joinDialog(url);

    }

    //get waste Marker from database into MAP
    private void getLocation(String url){
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
                Toast.makeText(MapsActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void reflect(){
        searchViewLocation = (SearchView) findViewById(R.id.svLocation);
        btn_location = (ImageButton) findViewById(R.id.btn_location);
        ibtn_report = (ImageButton) findViewById(R.id.ibtn_report);
        ibtn_clean = (ImageButton) findViewById(R.id.ibtn_clean);
        ibtn_rank = (ImageButton) findViewById(R.id.ibtn_rank);
        ibtn_account = (ImageButton) findViewById(R.id.ibtn_account);
        txt_distance = (TextView) findViewById(R.id.txt_distance);
        txt_duration = (TextView) findViewById(R.id.txt_duration);
    }
    private void searchLocation(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchViewLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    //delete the old marker
                    if (markerSearch != null){ //nếu không kiểm tra đk này thì lúc đầu chưa có sẽ xóa luôn marker => k có marker này để mark nữa
                        markerSearch.remove();
                    }
                    //
                    String location = searchViewLocation.getQuery().toString();
                    List <Address> addressList = null;
                    if(location != null || !location.equals("")) {
                        try {
                            addressList = geocoder.getFromLocationName(location,1);
                        } catch (Exception e) {
                        }
                        if (addressList.size() > 0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            //getThroughfare : get street name
                            markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        } else {
                            List <Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongtitude, 1);
                            String str = addresses.get(0).getLocality();
                            str = str + " " + addresses.get(0).getAdminArea();
                            str = str + " " + addresses.get(0).getCountryName();

                            location = location.concat(",");
                            location = location + " " + str;

                            addressList = geocoder.getFromLocationName(location, 1);
                            Address address = addressList.get(0);

                            if(address.getThoroughfare() != null){
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }else{
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
  private void joinDialog(final String url){
            //set event for marker to display join dialog
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker m) {

                    //marker search can't join
                    if(markerSearch != null) {
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
                    bundle.putString("username",username);
                    bundle.putString("dateOfBirth",dateOfBirth);
                    bundle.putString("userJob",userJob);
                    bundle.putString("userGender",userGender);
                    bundle.putString("userID",userID);
                    bundle.putString("userScore",userScore);
                    Log.e("CheckABC",   userID +"-"
                            +dateOfBirth+"-"+userJob+"-"+userGender+"-"+userScore);
                    RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    double wasteLocation_latitude = Double.parseDouble(object.getString("waste_latitude"));
                                    double wasteLocation_longtitude = Double.parseDouble(object.getString("waste_longtitude"));
                                    if(wasteLocation_latitude == m.getPosition().latitude && wasteLocation_longtitude == m.getPosition().longitude){
                                        String waste_people = object.getString("waste_biod");
                                        String waste_size = object.getString("waste_size");
                                        String waste_image = object.getString("waste_image");
                                        bundle.putString("waste_people", waste_people);
                                        bundle.putString("waste_size", waste_size);
                                        bundle.putString("waste_image",waste_image);
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
                            Toast.makeText(MapsActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonArrayRequest);
                    return true;
                }
            });
  }
    @Override
    public void onDirectionFinderStart() {
        if(polyLinePaths != null){
            for(Polyline polyLine : polyLinePaths){
                polyLine.remove();
            }
        }
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        onDirectionFinderStart();
        polyLinePaths = new ArrayList<>();
        for( Route route: routes){
            txt_duration.setText(route.duration + " m");
            txt_distance.setText(route.distance + " km");
          PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.RED).width(10);

            for( int i = 0; i <route.points.size();i++ ){
                polylineOptions.add(route.points.get(i));
            }
            polyLinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public void changeColorJoinMaker(double wasteJoinLat, double wasteJoinLon, String wasteID, String wasteAddress ) {
        for(int i = 0; i < markerWasteList.size(); i++){
                if(markerWasteList.get(i).getPosition().latitude == wasteJoinLat && markerWasteList.get(i).getPosition().longitude == wasteJoinLon){
                    markerWasteList.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    if(wasteID != null) {
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
    }

}

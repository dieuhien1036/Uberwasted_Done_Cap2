package com.example.ggmap_getlocationtextview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class joinDialog extends BottomSheetDialogFragment implements DirectionFinderListener {
    private BottomSheetListener mListener;
    private TextView txt_address;
    private double currentLatitude ;
    private double currentLongtitude ;
    private double wasteLatitude ;
    private double wasteLongtitude ;
    private ImageView img_wasted;
    private ImageButton btn_direction;
    private Button btn_join;
    private TextView txt_size;
    private TextView txt_people;
    public static final int MY_REQUEST_CODE = 100;
    private String URL = "http://10.10.51.193/upload/uploads/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_dialog_layout, container, false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reflect(view);

        //lấy giá trị từ Mapacivity qua
        final String waste_address = getArguments().getString("waste_address");
        final String waste_people = getArguments().getString("waste_people");
        final String waste_size = getArguments().getString("waste_size");
        final String userID = getArguments().getString("userID");
        final String username = getArguments().getString("username");
        final String dateOfBirth = getArguments().getString("dateOfBirth");
        final String userScore = getArguments().getString("userScore");
        final String userJob = getArguments().getString("userJob");
        final String userGender = getArguments().getString("gender");
        final String waste_image = getArguments().getString("waste_image").trim();

        currentLatitude = getArguments().getDouble("currentLatitude",0);
        currentLongtitude = getArguments().getDouble("currentLongtitude",0);
        wasteLatitude = getArguments().getDouble("wasteLatitude",0);
        wasteLongtitude = getArguments().getDouble("wasteLongtitude",0);

        //set giá trị cho joinDialog.
        txt_address.setText(waste_address);
        txt_people.setText(waste_people);
        txt_size.setText(waste_size);

        //Load image của this waste vào dialog
        final String image_url = URL + waste_image;
        new LoadImages().execute(image_url);

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), JoinActivity.class);
                intent.putExtra("waste_address", waste_address);
                intent.putExtra("waste_people",waste_people);
                intent.putExtra("waste_size",waste_size);
                intent.putExtra("userID",userID);
                intent.putExtra("username",username);
                intent.putExtra("dateOfBirth",dateOfBirth);
                intent.putExtra("userJob",userJob);
                intent.putExtra("userGender",userGender);
                intent.putExtra("userGender",userScore);
                intent.putExtra("wasteLatitude",wasteLatitude);
                intent.putExtra("wasteLongtitude",wasteLongtitude);
                startActivityForResult(intent,MY_REQUEST_CODE);
            }
        });

        btn_direction = view.findViewById(R.id.btn_direction);
        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestDirection();
                onStop();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ) {
             wasteLatitude = data.getDoubleExtra("wasteLat",0);
             wasteLongtitude = data.getDoubleExtra("wasteLong",0);
             String wastedID = data.getStringExtra("wasteID");
             String wasteAddress = data.getStringExtra("wasteAddress");
             mListener.changeColorJoinMaker(wasteLatitude,wasteLongtitude,wastedID,wasteAddress);
             onStop();
        }
    }

    private void reflect(View view){
        img_wasted = view.findViewById(R.id.img_wasted);
        txt_address = view.findViewById(R.id.txt_address);
        btn_join = view.findViewById(R.id.btn_join);
        txt_size = view.findViewById(R.id.txt_size);
        txt_people = view.findViewById(R.id.txt_people);
    }

    public void sendRequestDirection(){
        DirectionFinder a = new DirectionFinder(this,currentLatitude,currentLongtitude,wasteLatitude,wasteLongtitude);
        a.execute();
    }

    @Override
    public void setText(List<Route> routes) {
        mListener.onDirectionFinderSuccess(routes);
    }

    public interface BottomSheetListener {
        void onDirectionFinderStart();
        void onDirectionFinderSuccess(List<Route> routes);
        void changeColorJoinMaker(double wasteJoinLat, double wasteJoinLon, String wasteID, String wasteAdress);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }

    //class doc du lieu anh
    private class LoadImages extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmaphinh;
        InputStream inputStream = null;
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                try {
                    inputStream = url.openConnection().getInputStream();
                    bitmaphinh = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return bitmaphinh;
        }

       @Override //hien thi anh len imageview
       protected void onPostExecute(Bitmap bitmap) {
           super.onPostExecute(bitmap);
           img_wasted.setImageBitmap(bitmaphinh);
       }


       private  View view;
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.join_dialog_layout, container, false);
            return view;
        }

    }
}

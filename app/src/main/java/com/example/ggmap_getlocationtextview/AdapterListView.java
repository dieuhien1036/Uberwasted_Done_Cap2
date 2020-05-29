package com.example.ggmap_getlocationtextview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AdapterListView extends BaseAdapter {
    private int layout;
    private List<JoinClean> joincleanlist;
    private CleanActivity context;
    private joinDialog.BottomSheetListener mListener;
    private String urlCheckin;
    JoinClean joinClean;
    ViewHolder holder;


    public AdapterListView(@NonNull CleanActivity context, int layout, joinDialog.BottomSheetListener mListener, ArrayList<JoinClean> joinclean, String urlCheckin) {
        this.context = context;
        this.layout = layout;
        this.mListener = mListener;
        this.joincleanlist = joinclean;
        this.urlCheckin = urlCheckin;
    }

    @Override
    public int getCount() {
        return joincleanlist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            holder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
            holder.btn_cancel = (Button) convertView.findViewById(R.id.btn_cancel);
            holder.btn_checkin = (Button) convertView.findViewById(R.id.btn_checkin);
            holder.btn_checkout = (Button) convertView.findViewById(R.id.btn_checkout);

            convertView.setTag(holder);
        }else{
                holder = (ViewHolder) convertView.getTag();
        }
            joinClean = joincleanlist.get(position);
            holder.txt_address.setText(joinClean.getWasteAddress());
            Log.e("BBB4",joinClean.getWasteAddress());
            holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDelete(joincleanlist.get(position).getJoin_id());
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mListener.changeColorJoinMaker(joincleanlist.get(position).getWasteLatitude(),joincleanlist.get(position).getWasteLongtitude(),null,null);
                        }
                    }, 3000);
                }
            });
            holder.btn_checkin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,Checkin.class);
                    intent.putExtra("wasteID",joincleanlist.get(position).getWaste_id());
                    intent.putExtra("userID",context.userID);
                    context.startActivity(intent);
                }
            });

            holder.btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   getDataCheckin(urlCheckin,position);
                }
            });

        return convertView;
    }
    private class ViewHolder{
        TextView txt_address;
        Button btn_cancel, btn_checkin,btn_checkout;
    }

    private void confirmDelete(final String join_id){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(context);
        dialogDelete.setMessage("Do you want to cancel clean this waste?");

        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               context.deleteJoin(join_id);
            }
        });
      /*  dialogDelete.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });*/
        dialogDelete.show();
    }
    Boolean OK = false;
    private void getDataCheckin(String urlCheckin, final int position){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlCheckin, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String join_id = object.getString("join_id");
                        if(join_id.equals(joincleanlist.get(position).getJoin_id())){
                            Intent intent = new Intent(context,Checkout.class);
                            intent.putExtra("wasteID",joincleanlist.get(position).getWaste_id());
                            intent.putExtra("userID",context.userID);
                            context.startActivity(intent);
                            OK = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if(OK == false){
                    Toast.makeText(context, "Can not checkout now!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        requestQueue.add(jsonArrayRequest);

    }


}

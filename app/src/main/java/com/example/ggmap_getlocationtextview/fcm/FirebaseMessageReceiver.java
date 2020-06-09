//package com.example.ggmap_getlocationtextview.fcm;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.util.Log;
//import android.widget.RemoteViews;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.RetryPolicy;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.ggmap_getlocationtextview.MapsActivity;
//import com.example.ggmap_getlocationtextview.R;
//import com.example.ggmap_getlocationtextview.SplashScreen;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class FirebaseMessageReceiver {
////        extends FirebaseMessagingService {
////    @RequiresApi(api = Build.VERSION_CODES.O)
////    @Override
////    public void onMessageReceived(RemoteMessage remoteMessage) {
////        //handle when receive notification via data event
////        if(remoteMessage.getData().size()>0){
////            showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
////        }
////        //handle when receive notification
////        if(remoteMessage.getNotification()!=null){
////            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
////        }
////
////    }
////
////    private RemoteViews getCustomDesign(String title, String message){
////        RemoteViews remoteViews=new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
////        remoteViews.setTextViewText(R.id.title,title);
////        remoteViews.setTextViewText(R.id.message,message);
////        remoteViews.setImageViewResource(R.id.icon,R.drawable.common_google_signin_btn_icon_light_normal);
////        return remoteViews;
////    }
////
////    @RequiresApi(api = Build.VERSION_CODES.O)
////    public void showNotification(String title, String message){
////        Intent intent=new Intent(this, SplashScreen.class);
////        String channel_id="web_app_channel";
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
////        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channel_id)
////                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light_normal)
////                .setSound(uri)
////                .setAutoCancel(true)
////                .setVibrate(new long[]{1000,1000,1000,1000,1000})
////                .setOnlyAlertOnce(true)
////                .setContentIntent(pendingIntent);
////
////        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
////            builder=builder.setContent(getCustomDesign(title,message));
////        }
////        else{
////            builder=builder.setContentTitle(title)
////                    .setContentText(message)
////                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_light_normal);
////        }
////        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
////            NotificationChannel notificationChannel=new NotificationChannel(channel_id,"web_app",NotificationManager.IMPORTANCE_HIGH);
////            notificationChannel.setSound(uri,null);
////            notificationManager.createNotificationChannel(notificationChannel);
////        }
////        notificationManager.notify(0,builder.build());
////    }
///*
//    private void sendFCMPush() {
//        MapsActivity m=new MapsActivity();
//        final String SERVER_KEY = "AAAA5X8ZDBE:APA91bHDkSbJW0In5hLU_8mOwP9zhNuD_E3WzYi8-0W0UT_rAdIPy3HrmaxNlP__KjmipMjaaJ08AqQeB591ynOeMEKj2k31e-bm1y1jFUq_HvhonynWJkJVEjoR6DojXts2MTtM_AQB";
//        Log.e("cccccc",String.valueOf(addressWaste));
//        String title = "Uber";
//        String msg = "Have new waste near " + String.valueOf(addressWaste);
//        String token = "epcG9vI67-E:APA91bFZH6i48Tm_6i2Ykf30JmHFjP0_rcv2Emqyc0ekk8GXTV4LtSc8DgxsjMrPJCJLnBqQBqRbGEzY7CuNYmIUwVgS1A0uTRUJgFRp_3O3-mYpYy4L4LaVGQi1XTVv1leadOuhEFJc";
//
//        JSONObject obj = null;
//        JSONObject objData = null;
//        JSONObject dataobjData = null;
//
//        try {
//            obj = new JSONObject();
//            objData = new JSONObject();
//
//            objData.put("body", msg);
//            objData.put("title", title);
//            objData.put("sound", "default");
//            objData.put("icon", "icon_name"); //   icon_name
//            objData.put("tag", "/topics/allDevices");
//            objData.put("priority", "high");
//
//            dataobjData = new JSONObject();
//            dataobjData.put("text", msg);
//            dataobjData.put("title", title);
//
//            obj.put("to", "/topics/allDevices");
//            //obj.put("priority", "high");
//
//            obj.put("notification", objData);
//            obj.put("data", dataobjData);
//            Log.e("return here>>", obj.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.e("True", response + "");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("False", error + "");
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "key=" + SERVER_KEY);
//                params.put("Content-Type", "application/json");
//                return params;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        int socketTimeout = 1000 * 60;// 60 seconds
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsObjRequest.setRetryPolicy(policy);
//        requestQueue.add(jsObjRequest);
//        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
//    }*/
//
//}

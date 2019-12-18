package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ForgotActivity extends AppCompatActivity {
    String message,mail,subject;
    EditText etEmail;
    ImageButton btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_main);

        btnSend = this.findViewById(R.id.btnSend);
        etEmail = this.findViewById(R.id.et_Email);

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendMail();
                /*Intent i = new Intent(MainActivity.this, CodeChecker.class);
                i.putExtra("mail", message);
                startActivity(i);*/
                Intent intent = new Intent(ForgotActivity.this, CodeChecker.class);
                Bundle extras = new Bundle();
                extras.putString("mail",message);
                extras.putString("email",mail);
                intent.putExtras(extras);
                startActivity(intent);

            }
        });
    }

    private void resetcodeafter5(int seconds)
    {
        TimerTask task = new TimerTask() {
            public void run() {
                //Toast.makeText(MainActivity.this,"abc",Toast.LENGTH_LONG).show();
                // or use whatever method you chose to generate the number...
                message = getAlphaNumericString(5);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, seconds*1000);
    }

    private void sendMail()
    {

        //After 5min => generate another random check
        //If message == etCheck =>       change password => generate another code
         mail = etEmail.getText().toString();
         message = getAlphaNumericString(5);
         subject = "Reset Password";

         resetcodeafter5(5);

        JavaMailAPI JavaMailAPI = new JavaMailAPI(this,mail,subject,message);

        JavaMailAPI.execute();


    }

    private String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}

package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CodeChecker extends AppCompatActivity {

    ImageButton btnSend;
    EditText etCode;
    String message,emailName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_checker);

        //Intent intent = getIntent();
        //message = intent.getStringExtra("mail");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
         emailName = extras.getString("email");
         message = extras.getString("mail");

        btnSend = this.findViewById(R.id.btn_Send);
        etCode = this.findViewById(R.id.et_Code);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = etCode.getText().toString();
                if(code.equals(message))
                {
                    //startActivity(new Intent(CodeChecker.this,PasswordChanger.class));
                    //Toast.makeText(CodeChecker.this,message,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CodeChecker.this, PasswordChanger.class);
                    i.putExtra("eName", emailName);
                    startActivity(i);

                }
                else Toast.makeText(CodeChecker.this,message,Toast.LENGTH_LONG).show();
            }
        });
    }
}

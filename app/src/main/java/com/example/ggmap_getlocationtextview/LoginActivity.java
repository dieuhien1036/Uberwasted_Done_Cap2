package com.example.ggmap_getlocationtextview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    EditText edt_Email;
    EditText edt_Pass;
    ImageButton bnt_Login;
    LinearLayout linearLayout;
    Button btn_ForgotPassword;
    Button btn_Register;
    String url = "http://192.168.1.5/androidwebservice/login.php";

    UserModel userModel = null;
    SharedPreferences sharedPreferences;
    DatabaseReference currentUserRef, counterRef;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LoginActivity", "onCreateView");
        setContentView(R.layout.login_activity);
        reflect();

        sharedPreferences = getSharedPreferences("DataLogin", MODE_PRIVATE);
        if (!sharedPreferences.getString("email", "").isEmpty() && !sharedPreferences.getString("password", "").isEmpty()) {
            Intent mapActivityIntent = new Intent(LoginActivity.this, MapsActivity.class);
            mapActivityIntent.putExtra("userID", sharedPreferences.getString("userID", ""));
            mapActivityIntent.putExtra("username", sharedPreferences.getString("username", ""));
            mapActivityIntent.putExtra("dateOfBirth", sharedPreferences.getString("dateOfBirth", ""));
            mapActivityIntent.putExtra("userScore", sharedPreferences.getString("userScore", ""));
            mapActivityIntent.putExtra("userJob", sharedPreferences.getString("userJob", ""));
            mapActivityIntent.putExtra("userGender", sharedPreferences.getString("userGender", ""));
            startActivity(mapActivityIntent);
        }
        bnt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_Email.getText().toString().trim();
                String password = edt_Pass.getText().toString().trim();
                userModel = new UserModel(email, password);
                if (checkEmptyFormat() == true) {

                    checkLogin(url);
                }

            }
        });
        btn_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkEmptyFormat() {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        boolean OK = true;

        //String passwordMD5=getMd5(userModel.getPassword());
        if (userModel.getEmail().isEmpty() || userModel.getPassword().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Input full information", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userModel.getEmail().matches(EMAIL_REGEX) == false) {
            Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userModel.getPassword().length() < 6 || userModel.getPassword().length() > 20) {
            Toast.makeText(LoginActivity.this, "Password must less than 6 and more than 20", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void checkLogin(String url) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(userModel.getEmail(), userModel.getPassword()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentHome = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intentHome);
                }
            }
        });
    }

    private void reflect() {
        edt_Email = (EditText) findViewById(R.id.edtEmail);
        edt_Pass = (EditText) findViewById(R.id.edtPass);
        bnt_Login = (ImageButton) findViewById(R.id.btn_Login);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        btn_ForgotPassword = (Button) findViewById(R.id.btn_ForgotPassword);
        btn_Register = findViewById(R.id.btn_register);
    }
}
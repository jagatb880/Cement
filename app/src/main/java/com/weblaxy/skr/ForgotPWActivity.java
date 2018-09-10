package com.weblaxy.skr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ForgotPWActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);

        Button loginButn = (Button)findViewById(R.id.login_butn);
        loginButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPWActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button signupButn = (Button)findViewById(R.id.signup_butn);
        signupButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPWActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button fpwButton = (Button)findViewById(R.id.fpw_butn);
        fpwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPWActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
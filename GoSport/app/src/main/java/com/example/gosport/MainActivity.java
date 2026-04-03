package com.example.gosport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.auth.LoginActivity;
import com.example.gosport.auth.RegisterActivity;
import com.example.gosport.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto redirect neu da login
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();
            if ("ADMIN".equals(role)) {
                startActivity(new Intent(this, com.example.gosport.admin.AdminActivity.class));
            } else {
                startActivity(new Intent(this, com.example.gosport.user.UserActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }
}

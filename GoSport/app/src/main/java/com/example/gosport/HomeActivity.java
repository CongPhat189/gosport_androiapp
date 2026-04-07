package com.example.gosport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.admin.AdminActivity;
import com.example.gosport.user.UserActivity;
import com.example.gosport.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcomeHome);
        tvWelcome.setText("Xin chào, " + sessionManager.getFullName() + "!");

        String role = sessionManager.getRole();

        if (role != null) {
            new Handler(getMainLooper()).postDelayed(() -> {
                if ("ADMIN".equals(role)) {
                    startActivity(new Intent(HomeActivity.this, AdminActivity.class));
                } else if ("USER".equals(role)) {
                    startActivity(new Intent(HomeActivity.this, UserActivity.class));
                }
                finish();
            }, 1000);
        }
    }
}

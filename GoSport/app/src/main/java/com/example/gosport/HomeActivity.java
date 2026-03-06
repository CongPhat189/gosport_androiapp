package com.example.gosport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.admin.AdminActivity;
import com.example.gosport.user.UserActivity;
import com.example.gosport.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnLogout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcomeHome);

        btnLogout = findViewById(R.id.btnLogout);


        tvWelcome.setText("Xin chào, " + sessionManager.getFullName() + "!");

        String role = sessionManager.getRole();

//        if (role != null) {
//
//            new Handler(getMainLooper()).postDelayed(() -> {
//
//                if (role.equals("ADMIN")) {
//                    startActivity(new Intent(HomeActivity.this, AdminActivity.class));
//                } else if (role.equals("USER")) {
//                    startActivity(new Intent(HomeActivity.this, UserActivity.class));
//                }
//
//                finish();
//
//            }, 1000);
//        }
        if (role != null) {

            new Handler(getMainLooper()).postDelayed(() -> {

                if ("ADMIN".equals(role)) {
                    startActivity(new Intent(HomeActivity.this, AdminActivity.class));
                }
                else if ("USER".equals(role)) {
                    startActivity(new Intent(HomeActivity.this, UserActivity.class));
                }
                finish();

            }, 1000);
        }

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

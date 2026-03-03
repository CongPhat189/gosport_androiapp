package com.example.gosport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

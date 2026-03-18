package com.example.gosport.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.gosport.R;
import com.example.gosport.auth.LoginActivity;
import com.example.gosport.utils.SessionManager;

public class UserActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    LinearLayout btnViewField, btnProfile, btnLogout, btnFielded;
    TextView tvUserName;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sessionManager = new SessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(sessionManager.getFullName());

        btnViewField = findViewById(R.id.btnViewField);
        btnProfile = findViewById(R.id.btnProfile);
        btnFielded = findViewById(R.id.btnFielded);
        btnLogout = findViewById(R.id.btnLogout);

        if (savedInstanceState == null) {
            loadFragment(new FieldListFragment());
        }

        btnViewField.setOnClickListener(v -> {
            loadFragment(new FieldListFragment());
            drawerLayout.closeDrawers();
        });

        btnProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            drawerLayout.closeDrawers();
        });

        btnFielded.setOnClickListener(v -> {
            loadFragment(new BookingUserFragment());
            drawerLayout.closeDrawers();
        });

        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {

                        sessionManager.logout();

                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
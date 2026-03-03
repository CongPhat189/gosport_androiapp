package com.example.gosport.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.gosport.R;
import com.example.gosport.utils.SessionManager;

public class AdminActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    LinearLayout btnUser, btnCategory, btnField, btnBooking, btnReport;
    TextView tvAdminName;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvAdminName = findViewById(R.id.tvAdminName);
        tvAdminName.setText(sessionManager.getFullName());

        btnUser = findViewById(R.id.btnUser);
        btnCategory = findViewById(R.id.btnCategory);
        btnField = findViewById(R.id.btnField);
        btnBooking = findViewById(R.id.btnBooking);
        btnReport = findViewById(R.id.btnReport);

        if (savedInstanceState == null) {
            loadFragment(new UserManagementFragment());
        }

        btnUser.setOnClickListener(v -> {
            loadFragment(new UserManagementFragment());
            drawerLayout.closeDrawers();
        });

        btnCategory.setOnClickListener(v -> {
            loadFragment(new CategoryManagementFragment());
            drawerLayout.closeDrawers();
        });

        btnField.setOnClickListener(v -> {
            loadFragment(new FieldManagementFragment());
            drawerLayout.closeDrawers();
        });

        btnBooking.setOnClickListener(v -> {
            loadFragment(new BookingManagementFragment());
            drawerLayout.closeDrawers();
        });

        btnReport.setOnClickListener(v -> {
            loadFragment(new ReportFragment());
            drawerLayout.closeDrawers();
        });
    }

    void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
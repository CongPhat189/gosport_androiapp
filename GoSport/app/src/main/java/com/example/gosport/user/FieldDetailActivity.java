package com.example.gosport.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.R;

public class FieldDetailActivity extends AppCompatActivity {

    TextView tvName, tvCategory, tvAddress, tvPrice, tvDescription, tvStatus,tvDate, tvTime;
    Button btnBook, btnDirection;

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        btnBook = findViewById(R.id.btnBook);
        btnDirection = findViewById(R.id.btnDirection);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        String name = getIntent().getStringExtra("fieldName");
        String category = getIntent().getStringExtra("categoryName");
        address = getIntent().getStringExtra("address");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("status");

        tvName.setText(name);
        tvCategory.setText("Loại: " + category);
        tvAddress.setText("Địa chỉ: " + address);
        tvPrice.setText("Giá: " + price + "đ/giờ");
        tvDescription.setText(description);
        tvStatus.setText("Trạng thái: " + status);

        tvDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {

                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvDate.setText(date);

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        tvTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {

                        String time = hourOfDay + ":" + minute;
                        tvTime.setText(time);

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );

            dialog.show();
        });
        // Nút đặt sân
        btnBook.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đặt sân sẽ làm tiếp 😎", Toast.LENGTH_SHORT).show();
        });

        btnDirection.setOnClickListener(v -> {

            if (address == null || address.trim().isEmpty()) {
                Toast.makeText(this, "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        });
    }


}
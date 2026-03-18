package com.example.gosport.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.adapter.BookingAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.BookingModel;
import com.example.gosport.utils.SessionManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<BookingModel> allBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        sessionManager = new SessionManager(this);

        initViews();
        loadBookingData();
        setupFilters();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingAdapter = new BookingAdapter();
        recyclerView.setAdapter(bookingAdapter);

        bookingAdapter.setListener(new BookingAdapter.OnBookingActionListener() {
            @Override
            public void onCancel(int bookingId, String startTime) {
                new AlertDialog.Builder(MyBookingsActivity.this)
                        .setTitle("Xác nhận hủy sân")
                        .setMessage("Bạn có chắc chắn muốn hủy lịch đặt sân này không?\n\nLưu ý: Chỉ có thể hủy trước giờ đá 24 tiếng.")
                        .setPositiveButton("Hủy sân", (dialog, which) -> {

                            DatabaseHelper dbHelper = new DatabaseHelper(MyBookingsActivity.this);

                            boolean isSuccess = dbHelper.cancelBookingWithRule(bookingId, startTime);

                            if (isSuccess) {
                                Toast.makeText(MyBookingsActivity.this, "Đã hủy sân thành công!", Toast.LENGTH_SHORT).show();

                                loadBookingData();

                                ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
                                chipGroup.check(R.id.chipAll);

                            } else {
                                Toast.makeText(MyBookingsActivity.this, "Hủy thất bại! Bạn chỉ được phép hủy trước 24 giờ.", Toast.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            }

            @Override
            public void onDirection(String address) {
                if (address != null && !address.trim().isEmpty()) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(MyBookingsActivity.this, "Sân này chưa cập nhật địa chỉ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupFilters() {
        ChipGroup chipGroupFilter = findViewById(R.id.chipGroupFilter);
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            // Gọi hàm lọc với ID của Chip đang được chọn
            applyFilter(checkedIds.get(0));
        });
    }

    private void applyFilter(int checkedId) {
        List<BookingModel> filteredList = new ArrayList<>();

        for (BookingModel model : allBookings) {
            String status = model.getStatus() != null ? model.getStatus() : "";

            if (checkedId == R.id.chipAll) {
                if (!"Cancelled".equals(status)) {
                    filteredList.add(model);
                }

            } else if (checkedId == R.id.chipUpcoming) {
                // SẮP TỚI: Lấy các đơn đang chờ duyệt (Pending), đã chốt (Confirmed), hoặc Checkin
                if ("Pending".equals(status) || "Confirmed".equals(status) || "Checkin".equals(status)) {
                    filteredList.add(model);
                }

            } else if (checkedId == R.id.chipCompleted) {
                // ĐÃ HOÀN THÀNH: Lấy status Completed
                if ("Completed".equals(status)) {
                    filteredList.add(model);
                }

            } else if (checkedId == R.id.chipCancelled) {
                // ĐÃ HỦY: Lấy status Cancelled
                if ("Cancelled".equals(status)) {
                    filteredList.add(model);
                }
            }
        }
        bookingAdapter.setBookings(filteredList);
    }

    private void loadBookingData() {

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        int userId = sessionManager.getUserId();

        Cursor cursor = dbHelper.getBookingsByUser(userId);

        List<BookingModel> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            do {

                int bookingId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("booking_id"));

                String fieldName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("field_name"));

                String startTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("start_time"));

                String endTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("end_time"));

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("total_price"));

                String status =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("status"));

                String address =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("address"));

                BookingModel model = new BookingModel(
                        bookingId,
                        userId,
                        0,
                        startTime,
                        endTime,
                        "",
                        price,
                        status,
                        "",
                        "",
                        fieldName,
                        address
                );

                list.add(model);

            } while (cursor.moveToNext());

            cursor.close();
        }

        allBookings = list;
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        int currentCheckedChipId = chipGroup.getCheckedChipIds().isEmpty() ? R.id.chipAll : chipGroup.getCheckedChipIds().get(0);

        applyFilter(currentCheckedChipId);
    }
}
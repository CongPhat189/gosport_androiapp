package com.example.gosport.admin;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.adapter.BookingAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.BookingModel;
import com.example.gosport.model.BookingModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private BookingAdapter adapter;
    private List<BookingModel> bookingList = new ArrayList<>();

    private String currentStatus = "Tất cả";
    private String dateFrom = "2025-01-01", dateTo = "2026-12-31"; // Mặc định khoảng rộng

    private MaterialButton btnDateFrom, btnDateTo;
    private TextView tvStatTotal, tvStatPending, tvStatCheckedIn, tvStatCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_management_admin, container, false);

        // Ánh xạ View
        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewAdminOrders);
        btnDateFrom = view.findViewById(R.id.btnPickDateFrom);
        btnDateTo = view.findViewById(R.id.btnPickDateTo);

        tvStatTotal = view.findViewById(R.id.tvStatTotal);
        tvStatPending = view.findViewById(R.id.tvStatPending);
        tvStatCheckedIn = view.findViewById(R.id.tvStatCheckedIn);
        tvStatCompleted = view.findViewById(R.id.tvStatCompleted);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Xử lý Tab Layout
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutStatus);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentStatus = tab.getText().toString();
                loadData();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 2. Xử lý chọn ngày
        btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        btnDateTo.setOnClickListener(v -> showDatePicker(false));

        // Nút Lọc (Nếu bạn có nút btnApplyFilter trong XML)
        if (view.findViewById(R.id.btnApplyFilter) != null) {
            view.findViewById(R.id.btnApplyFilter).setOnClickListener(v -> loadData());
        }

        loadData();
        return view;
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            String displayDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);

            if (isFromDate) {
                dateFrom = selectedDate;
                btnDateFrom.setText("Từ: " + displayDate);
            } else {
                dateTo = selectedDate;
                btnDateTo.setText("Đến: " + displayDate);
            }
            loadData();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadData() {
        bookingList.clear();
        String statusForDb;
        switch (currentStatus) {
            case "Chờ duyệt": statusForDb = "Pending"; break;
            case "Đã Check-in": statusForDb = "Checkin"; break;
            case "Hoàn thành": statusForDb = "Completed"; break;
            case "Đã hủy": statusForDb = "Cancelled"; break;
            default: statusForDb = "Tất cả"; break;
        }
        Cursor cursor = dbHelper.getBookingsAdminFiltered(dateFrom, dateTo, statusForDb);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                bookingList.add(new BookingModel(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter = new BookingAdapter(getContext(), bookingList, new BookingAdapter.OnStatusChangeListener() {
            @Override
            public void onStatusUpdated() {
                updateStatistics();
            }
        });
        recyclerView.setAdapter(adapter);

        updateStatistics();
    }

    private void updateStatistics() {
        int total = 0;
        int pending = 0;
        int checkin = 0;
        int completed = 0;

        // Duyệt qua danh sách hiện có để đếm
        for (BookingModel booking : bookingList) {
            total++;
            String status = booking.getStatus(); // Giả sử model của bạn có hàm getStatus()

            if ("Pending".equalsIgnoreCase(status)) {
                pending++;
            } else if ("Checkin".equalsIgnoreCase(status)) {
                checkin++;
            } else if ("Completed".equalsIgnoreCase(status)) {
                completed++;
            }
        }

        // Gán giá trị lên UI (Xóa bỏ các con số mặc định trong XML)
        tvStatTotal.setText(String.valueOf(total));
        tvStatPending.setText(String.valueOf(pending));
        tvStatCheckedIn.setText(String.valueOf(checkin));
        tvStatCompleted.setText(String.valueOf(completed));
    }
}
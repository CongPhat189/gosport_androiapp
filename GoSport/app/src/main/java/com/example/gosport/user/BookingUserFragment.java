package com.example.gosport.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.adapter.BookingUserAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.BookingModel;
import com.example.gosport.utils.SessionManager;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingUserFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingUserAdapter adapter;
    private List<BookingModel> bookingList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView tvTotal, tvPending, tvCompleted, tvDateRangeFrom, tvDateRangeTo;
    private LinearLayout layoutDateRange, layoutEmpty;


    private String dateFrom;
    private String dateTo;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_management_user, container, false);

        sessionManager = new com.example.gosport.utils.SessionManager(getContext());
        userId = sessionManager.getUserId();

        // 1. Ánh xạ View từ XML của bạn
        initViews(view);

        // 2. Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Xử lý logic ChipGroup Filter
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                dateFrom = "2020-01-01";
                dateTo = "2029-12-31";
                layoutDateRange.setVisibility(View.GONE);
                loadData();
            } else if (checkedId == R.id.chipMonth) {
                setCurrentMonthRange();
                layoutDateRange.setVisibility(View.GONE);
                loadData();
            } else if (checkedId == R.id.chipCustom) {
                layoutDateRange.setVisibility(View.VISIBLE);
                showDateRangePicker();
            }
        });

        // 4. Nút thay đổi ngày trong layoutDateRange
        view.findViewById(R.id.tvChangeDateRange).setOnClickListener(v -> showDateRangePicker());

        // Load dữ liệu lần đầu
        loadData();

        return view;
    }

    private void initViews(View v) {
        dbHelper = new DatabaseHelper(getContext());
        recyclerView = v.findViewById(R.id.recyclerViewOrders);
        tvTotal = v.findViewById(R.id.tvTotalOrders);
        tvPending = v.findViewById(R.id.tvPendingOrders);
        tvCompleted = v.findViewById(R.id.tvCompletedOrders);
        tvDateRangeFrom = v.findViewById(R.id.tvDateRangeFrom);
        tvDateRangeTo = v.findViewById(R.id.tvDateRangeTo);
        layoutDateRange = v.findViewById(R.id.layoutDateRange);
        layoutEmpty = v.findViewById(R.id.layoutEmptyState);
    }

    private void loadData() {
        bookingList.clear();
        int pendingCount = 0;
        int completedCount = 0;

        // Gọi hàm từ DatabaseHelper đã sửa logic date()
        Cursor cursor = dbHelper.getBookingsUserFiltered(userId, dateFrom, dateTo);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookingModel booking = new BookingModel(cursor);
                bookingList.add(booking);

                // Thống kê nhanh
                if (booking.getStatus().equalsIgnoreCase("Pending")) pendingCount++;
                if (booking.getStatus().equalsIgnoreCase("Completed")) completedCount++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Cập nhật Text cho CardView
        tvTotal.setText(String.valueOf(bookingList.size()));
        tvPending.setText(String.valueOf(pendingCount));
        tvCompleted.setText(String.valueOf(completedCount));

        // Xử lý hiển thị UI dựa trên kết quả
        if (bookingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter = new BookingUserAdapter(getContext(), bookingList, booking -> {
                showCancelConfirmationDialog(booking);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void showCancelConfirmationDialog(BookingModel booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy đặt sân")
                .setMessage("Bạn có chắc chắn muốn hủy đơn đặt sân này không?")
                .setPositiveButton("Có, hủy", (dialog, which) -> {
                    // Gọi hàm hủy từ DatabaseHelper. Lưu ý: Đảm bảo class BookingModel
                    // của bạn có trường bookingId hoặc hàm getBookingId()
                    boolean isCancelled = dbHelper.cancelBooking(booking.id);

                    if (isCancelled) {
                        Toast.makeText(getContext(), "Đã hủy đơn thành công", Toast.LENGTH_SHORT).show();
                        loadData(); // Tải lại danh sách để cập nhật giao diện
                    } else {
                        Toast.makeText(getContext(), "Hủy đơn thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void setCurrentMonthRange() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateFrom = sdf.format(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        dateTo = sdf.format(cal.getTime());
    }

    private void showDateRangePicker() {
        // Ví dụ đơn giản: Chọn ngày bắt đầu, sau đó chọn ngày kết thúc
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) -> {
            dateFrom = String.format("%d-%02d-%02d", year, month + 1, day);
            tvDateRangeFrom.setText(String.format("%02d/%02d/%d", day, month + 1, year));

            // Tiếp tục chọn ngày đến
            new DatePickerDialog(getContext(), (view1, year1, month1, day1) -> {
                dateTo = String.format("%d-%02d-%02d", year1, month1 + 1, day1);
                tvDateRangeTo.setText(String.format("%02d/%02d/%d", day1, month1 + 1, year1));
                loadData();
            }, year, month, day).show();

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}

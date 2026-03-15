package com.example.gosport.admin;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.adapter.StatAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.StatModel;
import com.google.android.material.tabs.TabLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportFragment extends Fragment {

    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    private DatabaseHelper dbHelper;

    private TextView tvSelectedDate, tvTotalOrders, tvTotalRevenue;
    private TextView tvBestMonth, tvBestMonthDetail; // QUAN TRỌNG: Hai biến này để xóa "Tháng 8"
    private TextView tvAvgOrders, tvAvgRevenue, tvOrderHeader, tvRevenueHeader;
    private BarChart barChartRevenue, barChartStatus;
    private RecyclerView rvOrders, rvRevenue;
    private View cardOrderChart, cardRevenueChart, cardCompletionChart;
    private TabLayout tabLayoutReport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistics, container, false);
        dbHelper = new DatabaseHelper(getContext());
        initViews(view);
        setupCharts();
        loadData(currentMonth, currentYear);

        // Click vào chữ "Năm..." hoặc "Tháng..." để mở bộ lọc
        tvSelectedDate.setOnClickListener(v -> showMonthYearPickerDialog());

        tabLayoutReport.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabVisibility(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void initViews(View v) {
        tvSelectedDate = v.findViewById(R.id.tvSelectedYear);
        tvTotalOrders = v.findViewById(R.id.tvTotalOrdersYear);
        tvTotalRevenue = v.findViewById(R.id.tvTotalRevenueYear);

        // Ánh xạ 2 ô đang bị lỗi hiển thị "Tháng 8"
        tvBestMonth = v.findViewById(R.id.tvBestMonth);
        tvBestMonthDetail = v.findViewById(R.id.tvBestMonthOrders);

        tvAvgOrders = v.findViewById(R.id.tvAvgOrders);
        tvAvgRevenue = v.findViewById(R.id.tvAvgRevenue);
        tvOrderHeader = v.findViewById(R.id.tvOrderHeader);
        tvRevenueHeader = v.findViewById(R.id.tvRevenueHeader);

        barChartRevenue = v.findViewById(R.id.barChartRevenue);
        barChartStatus = v.findViewById(R.id.barChartStatus);
        cardOrderChart = v.findViewById(R.id.cardOrderChart);
        cardRevenueChart = v.findViewById(R.id.cardRevenueChart);
        cardCompletionChart = v.findViewById(R.id.cardCompletionChart);
        tabLayoutReport = v.findViewById(R.id.tabLayoutReport);

        rvOrders = v.findViewById(R.id.recyclerMonthlyOrders);
        rvRevenue = v.findViewById(R.id.recyclerMonthlyRevenue);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRevenue.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadData(int month, int year) {
        if (tvSelectedDate == null) return;
        tvSelectedDate.setText("Tháng " + month + ", " + year);

        Cursor cursor = dbHelper.getMonthlyDetails(year);
        List<StatModel> statList = new ArrayList<>();
        List<BarEntry> revenueEntries = new ArrayList<>();
        List<BarEntry> statusEntries = new ArrayList<>();

        double totalRevYear = 0;
        int totalOrdersYear = 0;
        double maxRevInYear = 0;
        String monthWithMaxRev = "Không có";
        int ordersInMaxMonth = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String mStr = cursor.getString(0);
                    int mInt = Integer.parseInt(mStr);
                    int count = cursor.getInt(1);
                    double rev = cursor.getDouble(2); // Lấy giá trị gốc từ DB
                    int comp = cursor.getInt(3);
                    int canc = cursor.getInt(4);

                    statList.add(new StatModel(mStr, count, rev, comp));
                    totalOrdersYear += count;
                    totalRevYear += rev;

                    if (rev > maxRevInYear) {
                        maxRevInYear = rev;
                        monthWithMaxRev = "Tháng " + mStr;
                        ordersInMaxMonth = count;
                    }

                    // CHỈ vẽ lên biểu đồ tháng được chọn
                    if (mInt == month) {
                        // Vẽ giá trị thực tế lên biểu đồ (không chia triệu)
                        revenueEntries.add(new BarEntry(1f, (float) rev));
                        statusEntries.add(new BarEntry(1f, new float[]{comp, canc}));
                    }
                } catch (Exception e) { e.printStackTrace(); }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // --- CẬP NHẬT UI VỚI ĐỊNH DẠNG NGHÌN (XÓA ĐƠN VỊ TRIỆU) ---
        tvTotalOrders.setText(String.valueOf(totalOrdersYear));
        tvTotalRevenue.setText(String.format("%,.0fđ", totalRevYear)); // Định dạng: 37.200.000đ

        tvBestMonth.setText(monthWithMaxRev);
        tvBestMonthDetail.setText(ordersInMaxMonth + " đơn • " + String.format("%,.0fđ", maxRevInYear));

        tvAvgOrders.setText(String.format("%.1f đơn", (double) totalOrdersYear / 12));
        tvAvgRevenue.setText(String.format("%,.0fđ / tháng", totalRevYear / 12));

        tvOrderHeader.setText("Dữ liệu năm " + year);
        tvRevenueHeader.setText("Doanh thu tháng " + month + "/" + year);

        rvOrders.setAdapter(new StatAdapter(statList, false));
        rvRevenue.setAdapter(new StatAdapter(statList, true));

        updateCharts(revenueEntries, statusEntries);
    }

    private void updateTabVisibility(int position) {
        if (position == 0) {
            cardOrderChart.setVisibility(View.VISIBLE);
            cardCompletionChart.setVisibility(View.VISIBLE);
            cardRevenueChart.setVisibility(View.GONE);
        } else {
            cardOrderChart.setVisibility(View.GONE);
            cardCompletionChart.setVisibility(View.GONE);
            cardRevenueChart.setVisibility(View.VISIBLE);
        }
    }

    private void showMonthYearPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);
        builder.setView(dialogView);

        NumberPicker monthPicker = dialogView.findViewById(R.id.picker_month);
        NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1); monthPicker.setMaxValue(12); monthPicker.setValue(currentMonth);
        yearPicker.setMinValue(2020); yearPicker.setMaxValue(2030); yearPicker.setValue(currentYear);

        builder.setPositiveButton("Lọc", (dialog, which) -> {
            currentMonth = monthPicker.getValue();
            currentYear = yearPicker.getValue();
            loadData(currentMonth, currentYear);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateCharts(List<BarEntry> revEntries, List<BarEntry> statusEntries) {
        if (revEntries.isEmpty()) {
            barChartRevenue.clear();
            return;
        }

        BarDataSet revSet = new BarDataSet(revEntries, "Doanh thu (VNĐ)");
        revSet.setColor(Color.parseColor("#388E3C"));
        revSet.setValueTextSize(10f);
        // Format con số trên đầu cột biểu đồ sang định dạng nghìn
        revSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f", value);
            }
        });

        barChartRevenue.setData(new BarData(revSet));
        barChartRevenue.getXAxis().setDrawLabels(false);
        barChartRevenue.getAxisLeft().setDrawLabels(false); // Ẩn trục dọc vì số quá dài
        barChartRevenue.invalidate();

        // --- Cấu hình Biểu đồ Trạng thái (Tỉ lệ hoàn thành) ---
        BarDataSet statusSet = new BarDataSet(statusEntries, "");
        statusSet.setColors(new int[]{Color.parseColor("#1B5E20"), Color.parseColor("#F57F17")});
        statusSet.setStackLabels(new String[]{"Thành công", "Đã hủy"});
        statusSet.setValueTextColor(Color.WHITE);
        statusSet.setValueTextSize(12f);

        BarData statusData = new BarData(statusSet);
        barChartStatus.setData(statusData);

        XAxis xAxisStatus = barChartStatus.getXAxis();
        xAxisStatus.setDrawLabels(false);
        xAxisStatus.setDrawGridLines(false);

        barChartStatus.setFitBars(true);
        barChartStatus.animateY(1000);
        barChartStatus.invalidate();
    }

    private void setupCharts() {
        barChartRevenue.getDescription().setEnabled(false);
        barChartRevenue.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartRevenue.getAxisRight().setEnabled(false);
        barChartStatus.getDescription().setEnabled(false);
        barChartStatus.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartStatus.getAxisRight().setEnabled(false);
    }


}
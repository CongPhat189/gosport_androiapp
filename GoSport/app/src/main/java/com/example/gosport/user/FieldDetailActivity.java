package com.example.gosport.user;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.adapter.CalendarDayAdapter;
import com.example.gosport.adapter.TimeSlotAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.BookingModel;
import com.example.gosport.model.ReviewModel;
import com.example.gosport.review.ReviewActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FieldDetailActivity extends AppCompatActivity {

    // ── UI Components ────────────────────────────────────────────────────────
    private TextView tvName, tvAddress, tvPrice, tvDescription, tvCateField, tvStatusText;
    private MaterialCardView cardStatusBadge;
    private MaterialButton btnDaily, btnFixed, btnBook;
    private LinearLayout layoutDailyView, layoutFixedView;
    private RecyclerView rvCalendar, rvTimeSlots;
    private ImageButton btnBack;

    // Fixed Mode Specific UI
    private ChipGroup chipGroupDow;
    private TextView tvFixedStartDate, tvFixedEndDate;
    private MaterialCardView cardDateRangePicker;

    // Summary Card
    private MaterialCardView cardSummary;
    private TextView tvSummaryLabelDate, tvSummaryDate, tvSummarySlots;
    private TextView tvSummaryTotalLabel, tvSummaryTotal;

    // ── Review Components ───────────────────────────────────────────────────
    private TextView tvAverageRating, tvTotalReviews, tvViewAllReviews;
    private RatingBar ratingBarOverall;
    private ProgressBar progress5Star, progress4Star, progress3Star, progress2Star, progress1Star;
    private TextView tvPercent5, tvPercent4, tvPercent3, tvPercent2, tvPercent1;

    // Card review mới nhất
    private MaterialCardView cardReviewStats;
    private MaterialCardView cardLatestReviewItem;
    private TextView tvReviewerName, tvReviewDate, tvReviewContent;
    private RatingBar ratingBarLatest;

    private List<ReviewModel> reviewList;

    // ── Adapters ────────────────────────────────────────────────────────────
    private CalendarDayAdapter calendarAdapter;
    private TimeSlotAdapter timeSlotAdapter;

    // ── Data từ Intent ──────────────────────────────────────────────────────
    private int fieldId;
    private double pricePerHour;

    // ── State (Trạng thái đặt lịch) ─────────────────────────────────────────
    private boolean isDailyMode = true;

    // Dành cho Theo ngày
    private Date selectedDate = null;

    // Dành cho Cố định
    private final List<String> selectedDows = new ArrayList<>(); // "Mon", "Tue"...
    private Long fixedStartDate = null;
    private Long fixedEndDate = null;
    private DatabaseHelper databaseHelper;

    // ── Cấu hình Khung giờ & Ánh xạ Thứ ─────────────────────────────────────
    private static final List<String> ALL_SLOTS = Arrays.asList(
            "06:00","07:00","08:00","09:00","10:00","11:00",
            "12:00","13:00","14:00","15:00","16:00","17:00",
            "18:00","19:00","20:00","21:00"
    );

    private static final String[] DOW_VI_SHORT  = {"CN","T2","T3","T4","T5","T6","T7"};
    private static final String[] MONTHS_VI     = {
            "Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6",
            "Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"
    };

    private static final int[] DOW_CHIP_IDS = {
            R.id.chipMon, R.id.chipTue, R.id.chipWed,
            R.id.chipThu, R.id.chipFri, R.id.chipSat, R.id.chipSun
    };
    private static final String[] DOW_KEYS = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    private static final String[] DOW_LABELS = {"T2","T3","T4","T5","T6","T7","CN"};

    // ════════════════════════════════════════════════════════════════════════
    //  Lifecycle
    // ════════════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupToolbar();
        loadIntentData();
        setupToggleLogic();
        setupCalendar();
        setupTimeSlots();
        setupDowChips();
        setupClickListeners();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Init & Data Loading
    // ════════════════════════════════════════════════════════════════════════
    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvCateField = findViewById(R.id.cateField);

        cardStatusBadge = findViewById(R.id.tvStatusBadge);
        tvStatusText = cardStatusBadge.findViewById(R.id.tvStatusText);

        btnDaily = findViewById(R.id.btnDaily);
        btnFixed = findViewById(R.id.btnFixed);
        layoutDailyView = findViewById(R.id.layoutDailyView);
        layoutFixedView = findViewById(R.id.layoutFixedView);

        rvCalendar = findViewById(R.id.rvCalendar);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);

        chipGroupDow = findViewById(R.id.chipGroupDow);
        tvFixedStartDate = findViewById(R.id.tvFixedStartDate);
        tvFixedEndDate = findViewById(R.id.tvFixedEndDate);
        cardDateRangePicker = findViewById(R.id.cardDateRangePicker);

        btnBook = findViewById(R.id.btnBook);
        btnBack = findViewById(R.id.btnBack);

        cardSummary = findViewById(R.id.cardSummary);
        tvSummaryLabelDate = findViewById(R.id.tvSummaryLabelDate);
        tvSummaryDate = findViewById(R.id.tvSummaryDate);
        tvSummarySlots = findViewById(R.id.tvSummarySlots);
        tvSummaryTotalLabel = findViewById(R.id.tvSummaryTotalLabel);
        tvSummaryTotal = findViewById(R.id.tvSummaryTotal);

        // Review UI
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvTotalReviews = findViewById(R.id.tvTotalReviews);
        tvViewAllReviews = findViewById(R.id.tvViewAllReviews);
        ratingBarOverall = findViewById(R.id.ratingBarOverall);

        progress5Star = findViewById(R.id.progress5Star);
        progress4Star = findViewById(R.id.progress4Star);
        progress3Star = findViewById(R.id.progress3Star);
        progress2Star = findViewById(R.id.progress2Star);
        progress1Star = findViewById(R.id.progress1Star);

        tvPercent5 = findViewById(R.id.tvPercent5);
        tvPercent4 = findViewById(R.id.tvPercent4);
        tvPercent3 = findViewById(R.id.tvPercent3);
        tvPercent2 = findViewById(R.id.tvPercent2);
        tvPercent1 = findViewById(R.id.tvPercent1);

        cardReviewStats = findViewById(R.id.cardLatestReview);
        cardLatestReviewItem = findViewById(R.id.cardReviewContainer);

        tvReviewerName = findViewById(R.id.tvReviewerName);
        tvReviewDate = findViewById(R.id.tvReviewDate);
        tvReviewContent = findViewById(R.id.tvReviewContent);
        ratingBarLatest = findViewById(R.id.ratingBarLatest);
    }

    private void setupToolbar() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadIntentData() {
        Intent i = getIntent();
        if (i == null) return;

        fieldId = i.getIntExtra("fieldId", 0);
        pricePerHour = i.getDoubleExtra("price", 0);

        tvName.setText(i.getStringExtra("fieldName"));
        tvAddress.setText(i.getStringExtra("address"));
        tvDescription.setText(i.getStringExtra("description"));
        tvCateField.setText(i.getStringExtra("categoryName"));
        tvPrice.setText(formatPrice(pricePerHour) + "đ / giờ");

        updateStatusBadge(i.getStringExtra("status"));

        loadReviewData();
    }

    private void updateStatusBadge(String status) {
        boolean isAvailable = "available".equalsIgnoreCase(status) || "Còn sân".equalsIgnoreCase(status);
        if (isAvailable) {
            tvStatusText.setText("✓ Còn sân");
            tvStatusText.setTextColor(Color.parseColor("#2E7D32"));
            cardStatusBadge.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            tvStatusText.setText("✗ Hết sân");
            tvStatusText.setTextColor(Color.parseColor("#C62828"));
            cardStatusBadge.setCardBackgroundColor(Color.parseColor("#FFEBEE"));

            // Disable booking completely
            btnBook.setEnabled(true);
            btnBook.setAlpha(0.5f);
            btnBook.setText("Hiện tại đã hết sân");
            btnBook.setOnClickListener(v -> finish());

        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UI Toggle & Interaction Setups
    // ════════════════════════════════════════════════════════════════════════
    private void setupToggleLogic() {
        btnDaily.setOnClickListener(v -> switchMode(true));
        btnFixed.setOnClickListener(v -> switchMode(false));
        applyToggleUI();
    }

    private void switchMode(boolean daily) {
        if (isDailyMode == daily) return; // Không làm gì nếu ấn lại mode đang chọn

        isDailyMode = daily;

        // Reset toàn bộ state khi đổi mode
        selectedDate = null;
        selectedDows.clear();
        fixedStartDate = null;
        fixedEndDate = null;
        tvFixedStartDate.setText("dd/mm/yyyy");
        tvFixedEndDate.setText("dd/mm/yyyy");
        tvFixedStartDate.setTextColor(Color.parseColor("#BDBDBD"));
        tvFixedEndDate.setTextColor(Color.parseColor("#BDBDBD"));

        timeSlotAdapter.reset();
        uncheckAllDowChips();
        applyToggleUI();
        refreshSummary();

        // Nếu chuyển về theo ngày, load lại lịch trình
        if (isDailyMode) {
            setupCalendar();
        }
    }

    private void applyToggleUI() {
        int colorOn = Color.parseColor("#2f5d28");
        int colorOff = Color.parseColor("#EEEEEE");
        int textOn = Color.WHITE;
        int textOff = Color.parseColor("#757575");

        layoutDailyView.setVisibility(isDailyMode ? View.VISIBLE : View.GONE);
        layoutFixedView.setVisibility(isDailyMode ? View.GONE : View.VISIBLE);

        MaterialButton active = isDailyMode ? btnDaily : btnFixed;
        MaterialButton inactive = isDailyMode ? btnFixed : btnDaily;

        active.setBackgroundTintList(ColorStateList.valueOf(colorOn));
        active.setTextColor(textOn);
        active.setIconTint(ColorStateList.valueOf(textOn));

        inactive.setBackgroundTintList(ColorStateList.valueOf(colorOff));
        inactive.setTextColor(textOff);
        inactive.setIconTint(ColorStateList.valueOf(isDailyMode ? Color.parseColor("#6da8f2") : Color.parseColor("#5599e5")));
    }

    private void setupClickListeners() {
        cardDateRangePicker.setOnClickListener(v -> showDateRangePicker());

        btnBook.setEnabled(false);
        btnBook.setAlpha(0.5f);
        btnBook.setOnClickListener(v -> handleBooking());

        tvViewAllReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("FIELD_ID", fieldId);
            startActivity(intent);
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Material Date Range Picker (Dành cho Lịch Cố định)
    // ════════════════════════════════════════════════════════════════════════
    private void showDateRangePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now()); // Không cho chọn ngày trong quá khứ

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Chọn khoảng thời gian đặt sân");
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            fixedStartDate = selection.first;
            fixedEndDate = selection.second;

            // Xử lý TimeZone UTC do MaterialDatePicker trả về
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            String startStr = sdf.format(new Date(fixedStartDate));
            String endStr = sdf.format(new Date(fixedEndDate));

            tvFixedStartDate.setText(startStr);
            tvFixedEndDate.setText(endStr);
            tvFixedStartDate.setTextColor(Color.parseColor("#212121"));
            tvFixedEndDate.setTextColor(Color.parseColor("#212121"));

            refreshSummary();
        });

        picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Calendar & Time Slots Logic
    // ════════════════════════════════════════════════════════════════════════
    private void setupCalendar() {
        List<Date> days = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            days.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        calendarAdapter = new CalendarDayAdapter(days, date -> {
            selectedDate = date;
            timeSlotAdapter.reset();
            loadBookedSlotsForDate(date);
        });

        rvCalendar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCalendar.setAdapter(calendarAdapter);
    }

    private void loadBookedSlotsForDate(Date date) {
        // Xóa sạch list cũ
        List<BookingModel> bookedSlots = new ArrayList<>();

        // Format ngày được chọn thành yyyy-MM-dd để query DB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);

        android.database.Cursor cursor = databaseHelper.getBookingsForFieldOnDate(fieldId, dateString);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Lấy start_time từ cursor
                    String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));

                    // Tạo một BookingModel ảo chỉ chứa startTime để tương thích với TimeSlotAdapter
                    BookingModel b = new BookingModel();

                    // Vì TimeSlotAdapter.extractHourLabel() cắt string theo dấu ":",
                    // ta cần tách phần "HH:mm" ra khỏi chuỗi "yyyy-MM-dd HH:mm".
                    // Ví dụ: "2026-03-15 10:00:00" -> split(" ")[1] -> "10:00:00"
                    if(startTime != null && startTime.contains(" ")) {
                        b.setStartTime(startTime.split(" ")[1]);
                    } else {
                        b.setStartTime(startTime); // Fallback
                    }

                    bookedSlots.add(b);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Truyền list lấy từ DB vào Adapter
        timeSlotAdapter.setBookedFromBookings(bookedSlots);
        refreshSummary();
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(ALL_SLOTS, selectedLabels -> refreshSummary());
        rvTimeSlots.setLayoutManager(new GridLayoutManager(this, 3));
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void setupDowChips() {
        for (int i = 0; i < DOW_CHIP_IDS.length; i++) {
            Chip chip = findViewById(DOW_CHIP_IDS[i]);
            if (chip == null) continue;

            final String key = DOW_KEYS[i];
            chip.setOnCheckedChangeListener((btn, checked) -> {
                if (checked && !selectedDows.contains(key)) {
                    selectedDows.add(key);
                } else if (!checked) {
                    selectedDows.remove(key);
                }
                refreshSummary();
            });
        }
    }

    private void uncheckAllDowChips() {
        for (int id : DOW_CHIP_IDS) {
            Chip chip = findViewById(id);
            if (chip != null) chip.setChecked(false);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Summary Card & Booking Validation
    // ════════════════════════════════════════════════════════════════════════
    private void refreshSummary() {
        List<String> slots = timeSlotAdapter.getSelectedLabels();

        if (isDailyMode) {
            // Chế độ Theo Ngày
            if (selectedDate == null || slots.isEmpty()) { hideSummary(); return; }

            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            String dateStr = DOW_VI_SHORT[cal.get(Calendar.DAY_OF_WEEK) - 1] + ", "
                    + cal.get(Calendar.DAY_OF_MONTH) + " "
                    + MONTHS_VI[cal.get(Calendar.MONTH)];

            tvSummaryLabelDate.setText("Ngày đặt:");
            tvSummaryDate.setText(dateStr);
            tvSummarySlots.setText(String.join(", ", slots));
            tvSummaryTotalLabel.setText("Tổng cộng:");
            tvSummaryTotal.setText(formatPrice(slots.size() * pricePerHour) + "đ");

        } else {
            // Chế độ Cố Định
            if (selectedDows.isEmpty() || slots.isEmpty() || fixedStartDate == null) { hideSummary(); return; }

            List<String> dowDisplay = new ArrayList<>();
            for (int i = 0; i < DOW_KEYS.length; i++) {
                if (selectedDows.contains(DOW_KEYS[i])) dowDisplay.add(DOW_LABELS[i]);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String rangeStr = sdf.format(new Date(fixedStartDate)) + " - " + sdf.format(new Date(fixedEndDate));

            tvSummaryLabelDate.setText("Lịch cố định:");
            tvSummaryDate.setText(String.join(", ", dowDisplay) + " (" + rangeStr + ")");
            tvSummarySlots.setText(String.join(", ", slots));
            tvSummaryTotalLabel.setText("Tạm tính / tuần:");

            double perWeek = (double) selectedDows.size() * slots.size() * pricePerHour;
            tvSummaryTotal.setText(formatPrice(perWeek) + "đ");
        }

        showSummary();
    }

    private void showSummary() {
        cardSummary.setVisibility(View.VISIBLE);
        btnBook.setEnabled(true);
        btnBook.setAlpha(1f);
    }

    private void hideSummary() {
        cardSummary.setVisibility(View.GONE);
        btnBook.setEnabled(false);
        btnBook.setAlpha(0.5f);
    }

    private void handleBooking() {
        List<String> selectedSlots = timeSlotAdapter.getSelectedLabels();

        // Kiểm tra an toàn
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ thi đấu", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BookingConfirmActivity.class);
        intent.putExtra("FIELD_ID", fieldId);
        intent.putExtra("FIELD_NAME", tvName.getText().toString());
        intent.putExtra("PRICE_PER_HOUR", pricePerHour);
        intent.putStringArrayListExtra("SELECTED_SLOTS", new ArrayList<>(selectedSlots));

        if (isDailyMode) {
            if (selectedDate == null) return;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            intent.putExtra("BOOKING_TYPE", "DAILY");
            intent.putExtra("SELECTED_DATE", sdf.format(selectedDate));
        } else {
            if (selectedDows.isEmpty() || fixedStartDate == null || fixedEndDate == null) return;
            intent.putExtra("BOOKING_TYPE", "FIXED");
            intent.putStringArrayListExtra("SELECTED_DOWS", new ArrayList<>(selectedDows));
            intent.putExtra("FIXED_START_DATE", fixedStartDate);
            intent.putExtra("FIXED_END_DATE", fixedEndDate);
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "LỖI: Chưa khai báo BookingConfirmActivity trong Manifest!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,d", (long) price).replace(",", ".");
    }

    private void loadReviewData() {
        // Lấy danh sách review từ database
        reviewList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = databaseHelper.getReviewsByField(fieldId);

            if (cursor != null && cursor.moveToFirst()) {
                // Đọc tất cả reviews vào list
                do {
                    ReviewModel review = new ReviewModel();
                    review.setReviewId(cursor.getInt(cursor.getColumnIndexOrThrow("review_id")));
                    review.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    review.setFieldId(cursor.getInt(cursor.getColumnIndexOrThrow("field_id")));
                    review.setRating(cursor.getInt(cursor.getColumnIndexOrThrow("rating")));
                    review.setComment(cursor.getString(cursor.getColumnIndexOrThrow("comment")));
                    review.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

                    // Lấy thêm tên người dùng từ cursor (đã join trong query)
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    // Có thể set thêm field fullName nếu muốn mở rộng ReviewModel

                    reviewList.add(review);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        // Cập nhật UI với dữ liệu từ reviewList
        updateReviewUI(reviewList);
    }

    private void updateReviewUI(List<ReviewModel> reviews) {
        // LUÔN HIỂN THỊ BẢNG THỐNG KÊ
        cardReviewStats.setVisibility(View.VISIBLE);

        if (reviews == null || reviews.isEmpty()) {
            // KHÔNG CÓ REVIEW -> Ẩn thẻ bình luận, reset thống kê về 0
            cardLatestReviewItem.setVisibility(View.GONE);

            tvAverageRating.setText("0.0");
            ratingBarOverall.setRating(0);
            tvTotalReviews.setText("Chưa có đánh giá");

            // Reset progress bars
            updateProgressBar(progress5Star, tvPercent5, 0, 1);
            updateProgressBar(progress4Star, tvPercent4, 0, 1);
            updateProgressBar(progress3Star, tvPercent3, 0, 1);
            updateProgressBar(progress2Star, tvPercent2, 0, 1);
            updateProgressBar(progress1Star, tvPercent1, 0, 1);
            return;
        }

        // CÓ REVIEW -> Tính toán thống kê
        int totalReviews = reviews.size();
        float totalScore = 0;
        int[] starCounts = new int[6]; // index 1-5

        for (ReviewModel review : reviews) {
            int rating = review.getRating();
            if (rating >= 1 && rating <= 5) {
                starCounts[rating]++;
                totalScore += rating;
            }
        }

        // Hiển thị đánh giá trung bình
        float avgRating = totalScore / totalReviews;
        tvAverageRating.setText(String.format(Locale.US, "%.1f", avgRating));
        ratingBarOverall.setRating(avgRating);
        tvTotalReviews.setText(totalReviews + " đánh giá");

        // Cập nhật progress bars
        updateProgressBar(progress5Star, tvPercent5, starCounts[5], totalReviews);
        updateProgressBar(progress4Star, tvPercent4, starCounts[4], totalReviews);
        updateProgressBar(progress3Star, tvPercent3, starCounts[3], totalReviews);
        updateProgressBar(progress2Star, tvPercent2, starCounts[2], totalReviews);
        updateProgressBar(progress1Star, tvPercent1, starCounts[1], totalReviews);

        // HIỂN THỊ REVIEW MỚI NHẤT
        ReviewModel latestReview = reviews.get(0);
        String userName = getUserNameById(latestReview.getUserId());

        cardLatestReviewItem.setVisibility(View.VISIBLE);
        tvReviewerName.setText(userName);
        tvReviewContent.setText(latestReview.getComment() != null ? latestReview.getComment() : "");
        ratingBarLatest.setRating(latestReview.getRating());

        // (Tùy chọn) Lấy 2 chữ cái đầu làm Avatar
        TextView tvReviewerAvatar = findViewById(R.id.tvReviewerAvatar);
        if(userName != null && !userName.isEmpty()) {
            String[] words = userName.trim().split("\\s+");
            if(words.length >= 2) {
                String initials = words[words.length-2].substring(0,1) + words[words.length-1].substring(0,1);
                tvReviewerAvatar.setText(initials.toUpperCase());
            } else {
                tvReviewerAvatar.setText(words[0].substring(0, Math.min(2, words[0].length())).toUpperCase());
            }
        }

        // Format ngày
        try {
            SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date d = sdfDb.parse(latestReview.getCreatedAt());
            SimpleDateFormat sdfOut = new SimpleDateFormat("dd 'tháng' MM, yyyy", new Locale("vi", "VN"));
            tvReviewDate.setText(sdfOut.format(d));
        } catch (Exception e) {
            tvReviewDate.setText(latestReview.getCreatedAt());
        }
    }

    private void updateProgressBar(ProgressBar pb, TextView tv, int count, int totalReviews) {
        int percent = (totalReviews > 0) ? (count * 100) / totalReviews : 0;
        pb.setProgress(percent);
        tv.setText(percent + "%");
    }

    private String getUserNameById(int userId) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().rawQuery(
                    "SELECT " + DatabaseHelper.USER_FULL_NAME +
                            " FROM " + DatabaseHelper.TABLE_USERS +
                            " WHERE " + DatabaseHelper.USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return "Người dùng";
    }
}
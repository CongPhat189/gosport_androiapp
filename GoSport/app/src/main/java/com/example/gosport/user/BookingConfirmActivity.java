package com.example.gosport.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingConfirmActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    // ── UI Components ────────────────────────────────────────────────────────
    private ImageButton btnBack;
    private TextView tvSummaryTitle, tvFieldInfo, tvTotalPriceAmount;
    private TextView tvBookingId, tvBookingTime, tvBookingDate, tvDiscount;
    private EditText edtPromoCode;
    private MaterialButton btnApplyPromo, btnConfirmPayment, btnCancelTransaction;

    // Payment Methods
    private MaterialCardView cardPayCredit, cardPayBank, cardPayWallet, cardPayCash;
    private RadioButton radioPayCredit, radioPayBank, radioPayWallet, radioPayCash;
    private LinearLayout layoutQRCode;
    private TextView tvQRPrice;

    // ── Data Variables ──────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private int fieldId;
    private String fieldName;
    private double pricePerHour;
    private List<String> selectedSlots;
    private String bookingType; // "DAILY" or "FIXED"

    private String dailyDate; // yyyy-MM-dd
    private List<String> fixedDows;
    private Long fixedStartDate, fixedEndDate;

    private double totalPrice = 0;
    private String selectedPaymentMethod = "Bank"; // Mặc định chuyển khoản
    private String generatedBookingId;

    // Danh sách các ngày cụ thể sẽ được đặt (Dùng chung cho cả Daily và Fixed)
    private List<String> finalDatesToBook = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        getIntentData();
        setupPaymentMethods();
        setupButtons();

        calculateAndDisplayData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        tvFieldInfo = findViewById(R.id.tvFieldInfo);
        tvTotalPriceAmount = findViewById(R.id.tvTotalPriceAmount);

        tvBookingId = findViewById(R.id.tvBookingId);
        tvBookingTime = findViewById(R.id.tvBookingTime);
        tvBookingDate = findViewById(R.id.tvBookingDate);
//        tvDiscount = findViewById(R.id.tvDiscount);

//        edtPromoCode = findViewById(R.id.edtPromoCode);
//        btnApplyPromo = findViewById(R.id.btnApplyPromo);

//        cardPayCredit = findViewById(R.id.cardPayCredit);
//        cardPayWallet = findViewById(R.id.cardPayWallet);
        cardPayBank = findViewById(R.id.cardPayBank);

        cardPayCash = findViewById(R.id.cardPayCash);

//        radioPayCredit = findViewById(R.id.radioPayCredit);
//        radioPayWallet = findViewById(R.id.radioPayWallet);
        radioPayBank = findViewById(R.id.radioPayBank);
        radioPayCash = findViewById(R.id.radioPayCash);

        layoutQRCode = findViewById(R.id.layoutQRCode);
        tvQRPrice = findViewById(R.id.tvQRPrice);

        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCancelTransaction = findViewById(R.id.btnCancelTransaction);

        // Tạo mã đơn ảo
        generatedBookingId = "#SB" + System.currentTimeMillis();
        tvBookingId.setText(generatedBookingId);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;

        fieldId = intent.getIntExtra("FIELD_ID", -1);
        fieldName = intent.getStringExtra("FIELD_NAME");
        pricePerHour = intent.getDoubleExtra("PRICE_PER_HOUR", 0);
        selectedSlots = intent.getStringArrayListExtra("SELECTED_SLOTS");
        bookingType = intent.getStringExtra("BOOKING_TYPE");

        if ("DAILY".equals(bookingType)) {
            dailyDate = intent.getStringExtra("SELECTED_DATE");
        } else if ("FIXED".equals(bookingType)) {
            fixedDows = intent.getStringArrayListExtra("SELECTED_DOWS");
            fixedStartDate = intent.getLongExtra("FIXED_START_DATE", 0);
            fixedEndDate = intent.getLongExtra("FIXED_END_DATE", 0);
        }
    }

    private void calculateAndDisplayData() {
        if (selectedSlots == null || selectedSlots.isEmpty()) return;

        // 1. Xử lý thuật toán sinh ngày
        finalDatesToBook.clear();
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("EE, dd/MM/yyyy", new Locale("vi", "VN"));
        String dateDisplayText = "";

        if ("DAILY".equals(bookingType)) {
            finalDatesToBook.add(dailyDate); // Chỉ 1 ngày

            // Format ngày hiển thị
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyDate);
                dateDisplayText = sdfDisplay.format(d);
            } catch (Exception e) { e.printStackTrace(); }

        } else if ("FIXED".equals(bookingType)) {
            // Lịch cố định: Tìm tất cả các ngày khớp với thứ đã chọn trong khoảng thời gian
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fixedStartDate);
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(fixedEndDate);

            SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            while (!cal.after(endCal)) {
                String dayOfWeek = getDowStringFromCalendar(cal.get(Calendar.DAY_OF_WEEK));
                if (fixedDows.contains(dayOfWeek)) {
                    finalDatesToBook.add(sdfDb.format(cal.getTime()));
                }
                cal.add(Calendar.DAY_OF_MONTH, 1); // Tăng lên 1 ngày
            }

            dateDisplayText = "Lịch cố định (" + finalDatesToBook.size() + " buổi)";
        }

        // 2. Tính tổng tiền
        totalPrice = pricePerHour * selectedSlots.size() * finalDatesToBook.size();

        // 3. Xử lý hiển thị giờ (Chỉ lấy giờ đầu và cuối cho gọn)
        String timeDisplay = selectedSlots.get(0);
        if (selectedSlots.size() > 1) {
            String lastSlot = selectedSlots.get(selectedSlots.size() - 1);
            timeDisplay = selectedSlots.get(0) + " - " + getEndTimeString(lastSlot);
        } else {
            timeDisplay = selectedSlots.get(0) + " - " + getEndTimeString(selectedSlots.get(0));
        }

        // 4. Gắn lên UI
        tvFieldInfo.setText(fieldName + " • " + selectedSlots.size() + " giờ/buổi");
        tvTotalPriceAmount.setText(formatPrice(totalPrice) + "đ");

        tvBookingTime.setText(timeDisplay);
        tvBookingDate.setText(dateDisplayText);
        tvQRPrice.setText(formatPrice(totalPrice) + " VNĐ");

        generateVietQR(totalPrice, generatedBookingId);
    }

    private void setupPaymentMethods() {

        updatePaymentUI(cardPayBank, radioPayBank, "Bank");

        if (cardPayBank != null)
            cardPayBank.setOnClickListener(v -> updatePaymentUI(cardPayBank, radioPayBank, "Bank"));

        if (cardPayCash != null)
            cardPayCash.setOnClickListener(v -> updatePaymentUI(cardPayCash, radioPayCash, "Cash"));

    }

    private void updatePaymentUI(MaterialCardView selectedCard, RadioButton selectedRadio, String method) {
        selectedPaymentMethod = method;

        // Reset tất cả về màu xám
        if (cardPayCredit != null) resetCardStyle(cardPayCredit, radioPayCredit);
        if (cardPayWallet != null) resetCardStyle(cardPayWallet, radioPayWallet);
        if (cardPayBank != null) resetCardStyle(cardPayBank, radioPayBank);
        if (cardPayCash != null) resetCardStyle(cardPayCash, radioPayCash);

        // Đổi màu thẻ được chọn sang xanh
        selectedCard.setStrokeColor(Color.parseColor("#4CAF50"));
        selectedCard.setCardBackgroundColor(Color.parseColor("#F1F8E9"));
        selectedRadio.setChecked(true);

        // Ẩn/Hiện mã QR
        if (method.equals("Bank") || method.equals("Wallet")) {
            layoutQRCode.setVisibility(View.VISIBLE);
        } else {
            layoutQRCode.setVisibility(View.GONE);
        }
    }

    private void resetCardStyle(MaterialCardView card, RadioButton radio) {
        if (card == null || radio == null) return;

        card.setStrokeColor(Color.parseColor("#E0E0E0"));
        card.setCardBackgroundColor(Color.WHITE);
        radio.setChecked(false);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnCancelTransaction.setOnClickListener(v -> finish());

        btnConfirmPayment.setOnClickListener(v -> saveBookingToDatabase());
    }

    private void saveBookingToDatabase() {
        int currentUserId = sessionManager.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSuccess = true;
        double amountPerSlot = pricePerHour;

        String dbBookingType = "Daily";
        if ("FIXED".equals(bookingType) || "Fixed".equals(bookingType)) {
            dbBookingType = "Fixed";
        }

        for (String dateStr : finalDatesToBook) {
            for (String slot : selectedSlots) {
                String startTime = dateStr + " " + slot + ":00";
                String endTime = dateStr + " " + getEndTimeString(slot) + ":00";

                long bookingId = dbHelper.insertBooking(
                        currentUserId,
                        fieldId,
                        startTime,
                        endTime,
                        dbBookingType,
                        amountPerSlot,
                        ""
                );

                if (bookingId != -1) {
                    // ===== PAYMENT =====
                    if (selectedPaymentMethod.equals("Cash")) {
                        dbHelper.insertPayment(
                                (int) bookingId,
                                amountPerSlot,
                                "Cash",
                                generatedBookingId,
                                "Pending"
                        );
                    } else { // Chuyển khoản (Bank)
                        // Giả lập thanh toán Bank thành công
                        dbHelper.insertPayment(
                                (int) bookingId,
                                amountPerSlot,
                                "E-Wallet",
                                generatedBookingId,
                                "Completed"
                        );
                        dbHelper.confirmBankBooking((int) bookingId);
                    }
                } else {
                    isSuccess = false;
                }
            }
        }

        if (isSuccess) {
            Toast.makeText(this, "Đặt sân thành công", Toast.LENGTH_LONG).show();
            finish();


        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu vào hệ thống", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Helper Methods ──────────────────────────────────────────────────────

    private String getEndTimeString(String startTimeHour) {
        // Cộng thêm 1 tiếng vào giờ bắt đầu
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date d = df.parse(startTimeHour);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.HOUR_OF_DAY, 1);
            return df.format(cal.getTime());
        } catch (Exception e) {
            return "23:59";
        }
    }

    private String getDowStringFromCalendar(int calendarDayOfWeek) {
        switch (calendarDayOfWeek) {
            case Calendar.MONDAY: return "Mon";
            case Calendar.TUESDAY: return "Tue";
            case Calendar.WEDNESDAY: return "Wed";
            case Calendar.THURSDAY: return "Thu";
            case Calendar.FRIDAY: return "Fri";
            case Calendar.SATURDAY: return "Sat";
            case Calendar.SUNDAY: return "Sun";
            default: return "";
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,d", (long) price).replace(",", ".");
    }

    private void generateVietQR(double amount, String orderId) {
        // Thông tin tài khoản nhận tiền
        String bankId = "VCB"; // Tên viết tắt ngân hàng (VD: MB, VCB, TCB, ACB, TPB...)
        String accountNo = "1234567890"; // Số tài khoản
        String accountName = "TRAN TUAN THANG"; // Tên chủ tài khoản (Viết hoa không dấu)

        String addInfo = "Thanh toan don " + orderId;

        // Xử lý encode URL (Vì đường link không được chứa dấu cách)
        try {
            addInfo = java.net.URLEncoder.encode(addInfo, "UTF-8");
            accountName = java.net.URLEncoder.encode(accountName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tạo đường link API
        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact2.png" +
                "?amount=" + (long) amount +
                "&addInfo=" + addInfo +
                "&accountName=" + accountName;

        // Dùng Glide để load ảnh URL này vào ImageView imgQRCode
        ImageView imgQRCode = findViewById(R.id.imgQRCode);
        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_calendar) // Ảnh hiển thị tạm trong lúc chờ tải
                .error(android.R.drawable.ic_dialog_alert) // Ảnh hiển thị nếu tải lỗi
                .into(imgQRCode);
    }
}
package com.example.gosport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.BookingModel;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.AdminViewHolder> {

    private Context context;
    private List<BookingModel> bookingList;
    private DatabaseHelper dbHelper;
    private OnStatusChangeListener listener;

    // 1. Khai báo Interface
    public interface OnStatusChangeListener {
        void onStatusUpdated();
    }

    public BookingAdapter(Context context, List<BookingModel> bookingList, OnStatusChangeListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        // 1. Đổ dữ liệu cơ bản
        holder.tvOrderCode.setText(booking.orderCode);
        holder.tvStatus.setText(booking.status);
        holder.tvCustomerName.setText(booking.userName);
        holder.tvCustomerPhone.setText(booking.userPhone);
        holder.tvServiceName.setText(booking.fieldName);
        holder.tvPrice.setText(String.format("%,.0fđ", booking.totalPrice));

        if (booking.paymentMethod != null && !booking.paymentMethod.isEmpty()) {
            // Áp dụng Việt hóa cho phương thức thanh toán
            String method = booking.paymentMethod.equalsIgnoreCase("Cash") ? "Tiền mặt" : "Ví điện tử";
            holder.tvPaymentMethod.setText(method);
            holder.tvPaymentMethod.setVisibility(View.VISIBLE);
        } else {
            holder.tvPaymentMethod.setText("Chưa thanh toán");
            holder.tvPaymentMethod.setVisibility(View.VISIBLE);
        }

        // Tách ngày và giờ từ chuỗi start_time (Giả sử định dạng: YYYY-MM-DD HH:mm)
        holder.tvDate.setText(getFormattedDate(booking.startTime));
        holder.tvTime.setText(getFormattedTime(booking.startTime));
        holder.tvAddress.setText(booking.address);

        // 2. Xử lý Logic ẩn hiện nút bấm & Trạng thái hoàn thành
        updateUIBasedOnStatus(holder, booking.status);

        holder.btnCheckIn.setOnClickListener(v -> {
            if (dbHelper.updateBookingStatus(booking.id, "Checkin")) {
                booking.status = "Checkin";
                notifyItemChanged(position);

                // 4. Báo cho Fragment biết để update số liệu
                if (listener != null) listener.onStatusUpdated();

                Toast.makeText(context, "Check-in thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnComplete.setOnClickListener(v -> {
            if (dbHelper.updateBookingStatus(booking.id, "Completed")) {
                booking.status = "Completed";
                notifyItemChanged(position);

                // 4. Báo cho Fragment biết để update số liệu
                if (listener != null) listener.onStatusUpdated();

                Toast.makeText(context, "Đơn hàng đã hoàn tất!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIBasedOnStatus(AdminViewHolder holder, String status) {
        switch (status) {
            case "Pending":
                holder.layoutActions.setVisibility(View.VISIBLE);
                holder.layoutCompletedState.setVisibility(View.GONE);
                holder.btnCheckIn.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_orange);
                break;

            case "Checkin":
                holder.layoutActions.setVisibility(View.VISIBLE);
                holder.layoutCompletedState.setVisibility(View.GONE);
                holder.btnCheckIn.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_blue);
                break;

            case "Completed":
                holder.layoutActions.setVisibility(View.GONE);
                holder.layoutCompletedState.setVisibility(View.VISIBLE);
                holder.tvCompletedLabel.setText("Đơn đã hoàn thành");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_green);
                break;

            case "Cancelled":
                holder.layoutActions.setVisibility(View.GONE);
                holder.layoutCompletedState.setVisibility(View.VISIBLE);
                holder.tvCompletedLabel.setText("Đơn đã hủy");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_red);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private String getFormattedDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";
        try {
            // Nếu bạn đã dùng strftime trong SQL trả về dd/MM/yyyy thì chỉ cần split lấy phần đầu
            // Nếu SQL trả về yyyy-MM-dd HH:mm:ss thì dùng logic dưới đây
            String[] parts = dateTimeStr.split(" ");
            String datePart = parts[0];

            if (datePart.contains("-")) { // Định dạng yyyy-MM-dd
                String[] ymd = datePart.split("-");
                return ymd[2] + "/" + ymd[1] + "/" + ymd[0];
            }
            return datePart; // Trả về dd/MM/yyyy nếu SQL đã format sẵn
        } catch (Exception e) {
            return dateTimeStr;
        }
    }

    private String getFormattedTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";
        try {
            String[] parts = dateTimeStr.split(" ");
            if (parts.length > 1) {
                // Lấy HH:mm:ss và cắt bỏ :ss nếu có
                String time = parts[1];
                return time.length() > 5 ? time.substring(0, 5) : time;
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvStatus, tvAddress, tvCustomerName, tvCustomerPhone, tvPrice, tvServiceName, tvDate, tvTime, tvCompletedLabel, tvPaymentMethod;
        MaterialButton btnCheckIn, btnComplete;
        LinearLayout layoutActions, layoutCompletedState;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvAdminOrderCode);
            tvStatus = itemView.findViewById(R.id.tvAdminOrderStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvPrice = itemView.findViewById(R.id.tvAdminPrice);
            tvServiceName = itemView.findViewById(R.id.tvAdminServiceName);
            tvDate = itemView.findViewById(R.id.tvAdminDate);
            tvTime = itemView.findViewById(R.id.tvAdminTime);
            tvCompletedLabel = itemView.findViewById(R.id.tvCompletedLabel);

            // ÁNH XẠ ID MỚI Ở ĐÂY
            tvPaymentMethod = itemView.findViewById(R.id.tvAdminPaymentMethod);

            btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            layoutActions = itemView.findViewById(R.id.layoutAdminActions);
            layoutCompletedState = itemView.findViewById(R.id.layoutCompletedState);
            tvAddress = itemView.findViewById(R.id.tvAdminAddress);
        }
    }
}
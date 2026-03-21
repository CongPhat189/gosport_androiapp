package com.example.gosport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.model.BookingModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingUserAdapter extends RecyclerView.Adapter<BookingUserAdapter.ViewHolder> {

    private Context context;
    private List<BookingModel> list;
    private OnCancelClickListener cancelListener;

    public interface OnCancelClickListener {
        void onCancelClick(BookingModel booking);
    }

    public BookingUserAdapter(Context context, List<BookingModel> list, OnCancelClickListener cancelListener) {
        this.context = context;
        this.list = list;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel booking = list.get(position);

        // Đổ dữ liệu dựa trên ID XML mới
        holder.tvOrderCode.setText(booking.orderCode);
        holder.tvServiceName.setText(booking.fieldName);
        if (booking.startTime != null) {
            holder.tvOrderDate.setText(formatDate(booking.startTime));
        } // Lấy phần Ngày
        holder.tvOrderTime.setText(booking.startTime.split(" ")[1]); // Lấy phần Giờ
        holder.tvOrderPrice.setText(String.format("%,.0f VNĐ", booking.totalPrice));
        holder.tvOrderLocation.setText(booking.address);

        if (booking.paymentMethod != null) {
            holder.tvPaymentMethod.setText(booking.paymentMethod);
        } else {
            holder.tvPaymentMethod.setText("Chưa xác định");
        }

        if (booking.createdAt != null) {
            holder.tvCreatedAt.setText("Đặt lúc: " + formatDateTime(booking.createdAt));
        }

        // Trạng thái đơn hàng
        holder.tvOrderStatus.setText(translateStatus(booking.status));
        updateStatusBadge(holder.tvOrderStatus, booking.status);

        if ("Pending".equalsIgnoreCase(booking.status)) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnCancelOrder.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onCancelClick(booking);
                }
            });
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }
    }

    private void updateStatusBadge(TextView tv, String status) {
        // Thiết lập màu sắc dựa trên trạng thái
        switch (status) {
            case "Pending":
                tv.setBackgroundResource(R.drawable.bg_status_badge_orange); // Màu cam
                break;
            case "Confirmed":
                tv.setBackgroundResource(R.drawable.bg_status_badge_pink); // Màu vàng
                break;
            case "Checkin":
                tv.setBackgroundResource(R.drawable.bg_status_badge_blue); // Màu xanh dương
                break;
            case "Completed":
                tv.setBackgroundResource(R.drawable.bg_status_badge_green); // Màu xanh lá
                break;
            default:
                tv.setBackgroundResource(R.drawable.bg_status_badge_red); // Màu xám/đỏ
                break;
        }
    }

    private String translateStatus(String status) {
        if (status.equals("Pending")) return "Chờ duyệt";
        if (status.equals("Confirmed")) return "Đã xác nhận";
        if (status.equals("Checkin")) return "Đã Check-in";
        if (status.equals("Completed")) return "Hoàn thành";
        return "Đã hủy";
    }

    private String formatDateTime(String dateStr) {
        try {
            // Định dạng gốc của SQLite
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // Định dạng mong muốn: 15/01/2025 08:30
            SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            return sdfOut.format(sdfIn.parse(dateStr));
        } catch (Exception e) {
            return dateStr; // Nếu lỗi thì trả về chuỗi gốc để không bị crash
        }
    }

    private String formatDate(String dateStr) {
        try {
            // Định dạng gốc của SQLite
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // 15/01/2025 08:30
            SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            return sdfOut.format(sdfIn.parse(dateStr));
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderStatus, tvServiceName, tvOrderDate, tvOrderTime, tvOrderPrice, tvOrderLocation, tvCreatedAt, tvPaymentMethod;
        MaterialButton btnCancelOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderLocation = itemView.findViewById(R.id.tvOrderLocation);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}
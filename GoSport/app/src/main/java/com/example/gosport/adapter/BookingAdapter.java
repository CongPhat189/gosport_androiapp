package com.example.gosport.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.model.BookingModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingModel> bookingList = new ArrayList<>();

    // ===== LISTENER =====
    public interface OnBookingActionListener {
        void onCancel(int bookingId, String startTime);
        void onDirection(String address);
    }

    private OnBookingActionListener listener;

    public void setListener(OnBookingActionListener listener) {
        this.listener = listener;
    }

    // ===== SET DATA =====
    public void setBookings(List<BookingModel> bookings) {
        this.bookingList = bookings;
        notifyDataSetChanged();
    }

    // ===== CREATE VIEW =====
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    // ===== BIND =====
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        // 1. Tên sân
        holder.tvFieldName.setText(booking.getFieldName() != null ? booking.getFieldName() : "Sân chưa xác định");

        // 2. Mã đơn & Loại sân (Ghép ID vào cho giống mã Booking)
        holder.tvBookingDesc.setText("#SB" + booking.getBookingId() + " · Sân GoSport");

        // 3. Thời gian và Ngày (Nếu Model bạn chưa có Ngày riêng, tạm lấy createdAt hoặc gộp chung)
        String timeDisplay = booking.getStartTime() + " - " + booking.getEndTime();
        holder.tvTime.setText(timeDisplay);
        // Tạm thời gán chuỗi ngày, nếu bạn có hàm lấy ngày đặt thì thay vào đây:
        holder.tvDate.setText("Ngày đặt: " + (booking.getCreatedAt() != null ? booking.getCreatedAt() : "..."));

        // 4. Giá tiền (Format định dạng hàng nghìn có dấu chấm: 150.000đ)
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPrice.setText(formatter.format(booking.getTotalPrice()) + "đ");

        // 5. Trạng thái và Đổi màu Card tương ứng
        String status = booking.getStatus() != null ? booking.getStatus() : "";
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("Đã hủy")) {
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Đỏ nhạt
            holder.tvStatus.setTextColor(Color.parseColor("#C62828")); // Đỏ đậm
            holder.btnCancel.setVisibility(View.GONE); // Đã hủy thì ẩn nút Hủy đơn đi

        } else if (status.equalsIgnoreCase("Đã hoàn thành")) {
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#E0F7FA")); // Xanh dương nhạt
            holder.tvStatus.setTextColor(Color.parseColor("#00695C"));
            holder.btnCancel.setVisibility(View.GONE); // Đá xong rồi thì không được hủy nữa

        } else {
            // Mặc định cho "Đã xác nhận" / "Sắp tới"
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Xanh lá nhạt
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.btnCancel.setVisibility(View.VISIBLE); // Được phép hủy
        }

        // 6. Trạng thái thanh toán (Bạn có thể map điều kiện thật vào đây)
        holder.tvPaymentStatus.setText("Chi tiết"); // Hoặc "Đã thanh toán" / "Chưa thanh toán"

        // ===== XỬ LÝ CLICK NÚT =====
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel(booking.getBookingId(), booking.getStartTime());
            }
        });

        holder.btnDirection.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDirection(booking.getAddress());
            }
        });
    }

    // ===== COUNT =====
    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    // ===== VIEW HOLDER =====
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvStatus, tvBookingDesc, tvDate, tvTime, tvPrice, tvPaymentStatus;
        MaterialCardView cardStatus;
        MaterialButton btnDirection, btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBookingDesc = itemView.findViewById(R.id.tvBookingDesc);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);

            cardStatus = itemView.findViewById(R.id.cardStatus);
            btnDirection = itemView.findViewById(R.id.btnDirection);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
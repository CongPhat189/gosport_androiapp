package com.example.gosport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.model.BookingModel;
import java.util.List;

public class BookingUserAdapter extends RecyclerView.Adapter<BookingUserAdapter.ViewHolder> {

    private Context context;
    private List<BookingModel> list;

    public BookingUserAdapter(Context context, List<BookingModel> list) {
        this.context = context;
        this.list = list;
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
        holder.tvOrderDate.setText(booking.startTime.split(" ")[0]); // Lấy phần Ngày
        holder.tvOrderTime.setText(booking.startTime.split(" ")[1]); // Lấy phần Giờ
        holder.tvOrderPrice.setText(String.format("%,.0f VNĐ", booking.totalPrice));

        // Trạng thái đơn hàng
        holder.tvOrderStatus.setText(translateStatus(booking.status));
        updateStatusBadge(holder.tvOrderStatus, booking.status);
    }

    private void updateStatusBadge(TextView tv, String status) {
        // Thiết lập màu sắc dựa trên trạng thái
        switch (status) {
            case "Pending":
                tv.setBackgroundResource(R.drawable.bg_status_badge_orange); // Màu cam
                break;
            case "Checkin":
                tv.setBackgroundResource(R.drawable.bg_status_badge_blue); // Màu xanh dương
                break;
            case "Completed":
                tv.setBackgroundResource(R.drawable.bg_status_badge_green); // Màu xanh lá
                break;
            default:
                tv.setBackgroundResource(R.drawable.circle_medium_green); // Màu xám/đỏ
                break;
        }
    }

    private String translateStatus(String status) {
        if (status.equals("Pending")) return "Chờ duyệt";
        if (status.equals("Checkin")) return "Đã Check-in";
        if (status.equals("Completed")) return "Hoàn thành";
        return "Đã hủy";
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderStatus, tvServiceName, tvOrderDate, tvOrderTime, tvOrderPrice, tvOrderLocation, tvCreatedAt;

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
        }
    }
}
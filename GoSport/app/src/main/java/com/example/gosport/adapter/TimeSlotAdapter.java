package com.example.gosport.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.model.BookingModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * TimeSlotAdapter
 *
 * Mỗi slot là một String label như "06:00", "07:00"...
 * Trạng thái slot được xác định bằng cách so sánh với danh sách BookingModel đã đặt.
 *
 * State:
 *   bookedLabels chứa label → hiển thị xám, gạch ngang
 *   selectedLabels chứa label → hiển thị xanh
 *   còn lại → trắng, có thể chọn
 */
public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.SlotViewHolder> {

    public interface OnSlotToggleListener {
        void onSlotToggled(List<String> selectedLabels);
    }

    private final List<String> allSlots;          // tất cả slot label: ["06:00","07:00",...]
    private final List<String> bookedLabels  = new ArrayList<>();  // đã có người đặt
    private final List<String> selectedLabels = new ArrayList<>();  // người dùng đang chọn
    private final OnSlotToggleListener listener;

    public TimeSlotAdapter(List<String> allSlots, OnSlotToggleListener listener) {
        this.allSlots = allSlots;
        this.listener = listener;
    }

    // ── Cập nhật booked từ danh sách BookingModel trả về API ──────────────────

    /**
     * Nhận danh sách booking của ngày được chọn.
     * Trích startTime (vd: "10:00:00" hoặc "10:00") để đánh dấu booked.
     */
    public void setBookedFromBookings(List<BookingModel> bookings) {
        bookedLabels.clear();
        if (bookings != null) {
            for (BookingModel b : bookings) {
                String label = extractHourLabel(b.getStartTime());
                if (label != null) bookedLabels.add(label);
            }
        }
        // Bỏ chọn các slot vừa bị đánh dấu booked
        selectedLabels.removeAll(bookedLabels);
        notifyDataSetChanged();
        if (listener != null) listener.onSlotToggled(new ArrayList<>(selectedLabels));
    }

    /** Xóa toàn bộ selection và booked, dùng khi đổi ngày hoặc đổi mode */
    public void reset() {
        bookedLabels.clear();
        selectedLabels.clear();
        notifyDataSetChanged();
        if (listener != null) listener.onSlotToggled(new ArrayList<>());
    }

    public List<String> getSelectedLabels() {
        return new ArrayList<>(selectedLabels);
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new SlotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        String label = allSlots.get(position);

        // Reset paint flag
        holder.tvTime.setPaintFlags(
                holder.tvTime.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvTime.setText(label);

        if (bookedLabels.contains(label)) {
            // Đã bị đặt: xám + gạch ngang
            holder.card.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.card.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.tvTime.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvTime.setPaintFlags(holder.tvTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.7f);
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);

        } else if (selectedLabels.contains(label)) {
            // Đang chọn: xanh đậm
            holder.card.setCardBackgroundColor(Color.parseColor("#2f5d28"));
            holder.card.setStrokeColor(Color.parseColor("#2f5d28"));
            holder.tvTime.setTextColor(Color.WHITE);
            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(v -> toggle(label));

        } else {
            // Còn trống
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.card.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.tvTime.setTextColor(Color.parseColor("#212121"));
            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(v -> toggle(label));
        }
    }

    private void toggle(String label) {
        if (selectedLabels.contains(label)) {
            selectedLabels.remove(label);
        } else {
            selectedLabels.add(label);
        }
        int idx = allSlots.indexOf(label);
        if (idx >= 0) notifyItemChanged(idx);
        if (listener != null) listener.onSlotToggled(new ArrayList<>(selectedLabels));
    }

    @Override
    public int getItemCount() { return allSlots == null ? 0 : allSlots.size(); }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Cắt "HH:mm" từ startTime dạng "HH:mm:ss" hoặc "HH:mm" (tùy API trả về).
     * Ví dụ: "10:00:00" → "10:00", "10:00" → "10:00"
     */
    private String extractHourLabel(String startTime) {
        if (startTime == null || startTime.isEmpty()) return null;
        // Nếu định dạng "HH:mm:ss" hoặc "HH:mm"
        String[] parts = startTime.split(":");
        if (parts.length >= 2) {
            return parts[0] + ":" + parts[1];
        }
        return startTime;
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvTime;

        SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            card   = (MaterialCardView) itemView;
            tvTime = itemView.findViewById(R.id.tvSlotTime);
        }
    }
}
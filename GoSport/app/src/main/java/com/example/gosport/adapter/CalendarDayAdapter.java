package com.example.gosport.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Hiển thị 14 ngày tới dưới dạng RecyclerView ngang.
 * Không dùng model custom — chỉ dùng java.util.Date.
 */
public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder> {

    public interface OnDaySelectedListener {
        void onDaySelected(Date date);
    }

    // index 0 = CN (Calendar.SUNDAY = 1), 1 = T2 ...
    private static final String[] DOW_VI = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};

    private final List<Date> days;
    private int selectedPosition = -1;
    private final OnDaySelectedListener listener;

    public CalendarDayAdapter(List<Date> days, OnDaySelectedListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Date date = days.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Calendar.DAY_OF_WEEK: 1=Sun, 2=Mon ... 7=Sat → index vào DOW_VI
        holder.tvDow.setText(DOW_VI[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        holder.tvDom.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        boolean isSelected = (position == selectedPosition);
        boolean isToday    = isSameDay(date, new Date());

        if (isSelected) {
            holder.card.setCardBackgroundColor(Color.parseColor("#2f5d28"));
            holder.card.setStrokeColor(Color.parseColor("#2f5d28"));
            holder.tvDow.setTextColor(Color.WHITE);
            holder.tvDom.setTextColor(Color.WHITE);
        } else if (isToday) {
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.card.setStrokeColor(Color.parseColor("#2f5d28"));
            holder.tvDow.setTextColor(Color.parseColor("#757575"));
            holder.tvDom.setTextColor(Color.parseColor("#2f5d28"));
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.card.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.tvDow.setTextColor(Color.parseColor("#757575"));
            holder.tvDom.setTextColor(Color.parseColor("#212121"));
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onDaySelected(date);
        });
    }

    @Override
    public int getItemCount() { return days == null ? 0 : days.size(); }

    /** Trả về ngày đang chọn, null nếu chưa chọn */
    public Date getSelectedDate() {
        if (selectedPosition >= 0 && selectedPosition < days.size())
            return days.get(selectedPosition);
        return null;
    }

    private boolean isSameDay(Date a, Date b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(a).equals(sdf.format(b));
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvDow, tvDom;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            card  = (MaterialCardView) itemView;
            tvDow = itemView.findViewById(R.id.tvDow);
            tvDom = itemView.findViewById(R.id.tvDom);
        }
    }
}
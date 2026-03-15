package com.example.gosport.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.model.StatModel;
import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    private List<StatModel> list;
    private boolean isRevenueMode; // true: Hiện doanh thu, false: Hiện số đơn

    public StatAdapter(List<StatModel> list, boolean isRevenueMode) {
        this.list = list;
        this.isRevenueMode = isRevenueMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monthly_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatModel item = list.get(position);

        // Hiển thị nhãn tháng từ model (Ví dụ: "Tháng 03")
        holder.tvMonth.setText(item.monthLabel);

        if (isRevenueMode) {
            // LUÔN hiển thị định dạng nghìn đầy đủ
            holder.tvValue.setText(String.format("%,.0fđ", item.revenue));
            holder.tvValue.setTextColor(Color.parseColor("#1B5E20"));
        } else {
            holder.tvValue.setText(item.bookingCount + " đơn");
            holder.tvValue.setTextColor(Color.parseColor("#424242"));
        }

//        // Tỉ lệ hoàn thành của RIÊNG tháng đó
//        if (item.bookingCount > 0) {
//            double ratio = ((double) item.completedOrders / item.bookingCount) * 100;
//            holder.tvPercentage.setText(String.format("%.0f%%", ratio));
//            holder.tvPercentage.setTextColor(ratio >= 100 ? Color.parseColor("#388E3C") : Color.parseColor("#F57F17"));
//        } else {
//            holder.tvPercentage.setText("0%");
//        }
//
//        // Growth (Tăng trưởng)
//        holder.tvGrowth.setText(item.growth != null ? item.growth : "0%");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvValue, tvGrowth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonthLabel);
            tvValue = itemView.findViewById(R.id.tvStatValue);
            tvGrowth = itemView.findViewById(R.id.tvGrowthBadge);
        }
    }
}
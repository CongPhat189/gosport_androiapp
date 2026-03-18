package com.example.gosport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.model.ReviewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ReviewModel> reviewList;

    public ReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);

        String name = review.getUserFullName();
        if (name == null || name.trim().isEmpty()) {
            name = "Người dùng ẩn danh";
        }
        holder.tvName.setText(name);

        String[] words = name.trim().split("\\s+");
        String initials = "";
        if (words.length >= 2) {
            initials = words[words.length - 2].substring(0, 1) + words[words.length - 1].substring(0, 1);
        } else {
            initials = words[0].substring(0, Math.min(2, words[0].length()));
        }
        holder.tvAvatar.setText(initials.toUpperCase());

        holder.ratingBar.setRating(review.getRating());
        holder.tvComment.setText(review.getComment() != null ? review.getComment() : "");

        try {
            SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date d = sdfDb.parse(review.getCreatedAt());
            SimpleDateFormat sdfOut = new SimpleDateFormat("dd 'tháng' MM, yyyy", new Locale("vi", "VN"));
            holder.tvDate.setText(sdfOut.format(d));
        } catch (Exception e) {
            holder.tvDate.setText(review.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvDate, tvComment;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
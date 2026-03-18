package com.example.gosport.review;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;
import com.example.gosport.adapter.ReviewAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.ReviewModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private int fieldId;
    private String fieldName;
    private DatabaseHelper databaseHelper;
    private RecyclerView rvAllReviews;
    private ReviewAdapter reviewAdapter;
    private MaterialButton btnWriteReview;
    private TextView tvAverageRating, tvTotalReviews;
    private RatingBar ratingBarOverall;
    private ProgressBar progress5Star, progress4Star, progress3Star, progress2Star, progress1Star;
    private TextView tvPercent5, tvPercent4, tvPercent3, tvPercent2, tvPercent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        fieldId = getIntent().getIntExtra("FIELD_ID", 0);
        fieldName = getIntent().getStringExtra("FIELD_NAME");
        if (fieldName == null) fieldName = "Sân bóng";

        databaseHelper = new DatabaseHelper(this);

        setupToolbar();
        initStatsViews();

        rvAllReviews = findViewById(R.id.rvAllReviews);
        rvAllReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvAllReviews.setAdapter(reviewAdapter);

        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnWriteReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("FIELD_ID", fieldId);
            intent.putExtra("FIELD_NAME", fieldName);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvToolbarTitle);
        tvTitle.setText("Đánh giá — " + fieldName);
    }

    private void initStatsViews() {
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvTotalReviews = findViewById(R.id.tvTotalReviews);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews();
    }

    private void loadReviews() {
        List<ReviewModel> reviews = new ArrayList<>();
        Cursor cursor = databaseHelper.getReviewsByField(fieldId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ReviewModel review = new ReviewModel();
                review.setRating(cursor.getInt(cursor.getColumnIndexOrThrow("rating")));
                review.setComment(cursor.getString(cursor.getColumnIndexOrThrow("comment")));
                review.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                review.setUserFullName(fullName);

                reviews.add(review);
            } while (cursor.moveToNext());
            cursor.close();
        }

        reviewAdapter.setReviews(reviews);
        updateReviewStats(reviews); // Cập nhật thống kê
    }

    // Hàm tính toán và cập nhật bảng thống kê
    private void updateReviewStats(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            tvAverageRating.setText("0.0");
            ratingBarOverall.setRating(0);
            tvTotalReviews.setText("Chưa có đánh giá");
            updateProgressBar(progress5Star, tvPercent5, 0, 1);
            updateProgressBar(progress4Star, tvPercent4, 0, 1);
            updateProgressBar(progress3Star, tvPercent3, 0, 1);
            updateProgressBar(progress2Star, tvPercent2, 0, 1);
            updateProgressBar(progress1Star, tvPercent1, 0, 1);
            return;
        }

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

        float avgRating = totalScore / totalReviews;
        tvAverageRating.setText(String.format(Locale.US, "%.1f", avgRating));
        ratingBarOverall.setRating(avgRating);
        tvTotalReviews.setText(totalReviews + " đánh giá");

        updateProgressBar(progress5Star, tvPercent5, starCounts[5], totalReviews);
        updateProgressBar(progress4Star, tvPercent4, starCounts[4], totalReviews);
        updateProgressBar(progress3Star, tvPercent3, starCounts[3], totalReviews);
        updateProgressBar(progress2Star, tvPercent2, starCounts[2], totalReviews);
        updateProgressBar(progress1Star, tvPercent1, starCounts[1], totalReviews);
    }

    private void updateProgressBar(ProgressBar pb, TextView tv, int count, int totalReviews) {
        int percent = (totalReviews > 0) ? (count * 100) / totalReviews : 0;
        pb.setProgress(percent);
        tv.setText(percent + "%");
    }
}
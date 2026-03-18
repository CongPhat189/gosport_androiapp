package com.example.gosport.review;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class WriteReviewActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private int fieldId;
    private DatabaseHelper databaseHelper;
    private RatingBar ratingBarInput;
    private TextView tvRatingText;
    private EditText edtComment;
    private MaterialButton btnSubmitReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        fieldId = getIntent().getIntExtra("FIELD_ID", 0);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ratingBarInput = findViewById(R.id.ratingBarInput);
        tvRatingText = findViewById(R.id.tvRatingText);
        edtComment = findViewById(R.id.edtComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        ratingBarInput.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int stars = (int) rating;
            switch (stars) {
                case 1: tvRatingText.setText("Rất tệ (1/5)"); break;
                case 2: tvRatingText.setText("Tệ (2/5)"); break;
                case 3: tvRatingText.setText("Bình thường (3/5)"); break;
                case 4: tvRatingText.setText("Tốt (4/5)"); break;
                case 5: tvRatingText.setText("Tuyệt vời (5/5)"); break;
                default: tvRatingText.setText("Chưa đánh giá");
            }
        });

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        int rating = (int) ratingBarInput.getRating();
        String comment = edtComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();

        long result = databaseHelper.insertReview(userId, fieldId, rating, comment);
        if (result != -1) {
            Toast.makeText(this, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình, quay lại màn danh sách
        } else {
            Toast.makeText(this, "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.gosport.model;

public class ReviewModel {
    private int reviewId;
    private int userId;
    private int fieldId;
    private int rating;
    private String comment;
    private String createdAt;

    private String userFullName;

    public ReviewModel() {}

    public ReviewModel(int reviewId, int userId, int fieldId,
                  int rating, String comment, String createdAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.fieldId = fieldId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // getter setter
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}
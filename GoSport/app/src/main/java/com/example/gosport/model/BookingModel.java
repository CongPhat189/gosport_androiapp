package com.example.gosport.model;

public class BookingModel {
    private int bookingId;
    private int userId;
    private int fieldId;
    private String startTime;
    private String endTime;
    private String bookingType;
    private double totalPrice;
    private String status;
    private String note;
    private String createdAt;
    private String fieldName;
    private String address;

    public BookingModel() {}

    public BookingModel(int bookingId, int userId, int fieldId, String startTime,
                        String endTime, String bookingType, double totalPrice,
                        String status, String note, String createdAt, String fieldName, String address) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.fieldId = fieldId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingType = bookingType;
        this.totalPrice = totalPrice;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.fieldName = fieldName;
        this.address = address;
    }

    // getter setter

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
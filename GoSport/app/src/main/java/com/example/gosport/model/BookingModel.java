package com.example.gosport.model;

import android.database.Cursor;

import com.example.gosport.database.DatabaseHelper;

public class BookingModel {
    public int id;
    public String orderCode; // Mã đơn: #ORD...
    public String userName;
    public String userPhone;
    public String fieldName;
    public String startTime;
    public String endTime;
    public double totalPrice;
    public String status;

    public BookingModel(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("booking_id"));
        this.orderCode = "#ORD-2026-" + String.format("%04d", id);
        this.startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        this.totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        this.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

        // Kiểm tra cột trước khi lấy - Giúp dùng chung an toàn
        int fieldNameIdx = cursor.getColumnIndex("field_name");
        if (fieldNameIdx != -1) this.fieldName = cursor.getString(fieldNameIdx);

        int userNameIdx = cursor.getColumnIndex("full_name");
        if (userNameIdx != -1) this.userName = cursor.getString(userNameIdx);

        int userPhoneIdx = cursor.getColumnIndex("phone_number");
        if (userPhoneIdx != -1) this.userPhone = cursor.getString(userPhoneIdx);
    }

    // Cần thêm các hàm này để Fragment/Adapter truy cập được
    public String getStatus() { return status; }
    public String getFieldName() { return fieldName; }
}

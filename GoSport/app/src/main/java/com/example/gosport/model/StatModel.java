package com.example.gosport.model;

public class StatModel {
    public String monthLabel;
    public int bookingCount;
    public double revenue;
    public String growth; // Ví dụ: "+8%"

    public StatModel(String month, int count, double rev) {
        this.monthLabel = "Tháng " + month;
        this.bookingCount = count;
        this.revenue = rev;
        this.growth = "0%"; // Bạn có thể tính toán logic so sánh tháng trước ở đây
    }
}

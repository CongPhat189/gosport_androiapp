package com.example.gosport.model;

public class StatModel {
    public String monthLabel;
    public int bookingCount;
    public double revenue;
    private int completedOrders; // Thêm biến này
    public String growth; // Ví dụ: "+8%"

    public StatModel(String month, int count, double rev,int comp) {
        this.monthLabel = "Tháng " + month;
        this.bookingCount = count;
        this.revenue = rev;
        this.growth = "0%"; // Bạn có thể tính toán logic so sánh tháng trước ở đây
        this.completedOrders = comp;
    }
    public int getCompletedOrders() { return completedOrders; }
}

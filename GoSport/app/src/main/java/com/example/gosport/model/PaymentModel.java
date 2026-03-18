package com.example.gosport.model;

public class PaymentModel {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMethod;
    private String transactionRef;
    private String paymentStatus;
    private String paymentTime;

    public PaymentModel() {}

    public PaymentModel(int paymentId, int bookingId, double amount,
                   String paymentMethod, String transactionRef,
                   String paymentStatus, String paymentTime) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionRef = transactionRef;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    // getter setter
}
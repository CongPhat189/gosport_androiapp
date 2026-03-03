package com.example.gosport.model;

public class FieldModel {

    private int fieldId;
    private int categoryId;
    private String categoryName;
    private String fieldName;
    private String address;
    private String description;
    private double pricePerHour;
    private String status;
    private String imageUrl;

    public FieldModel(int fieldId, int categoryId, String categoryName,
                      String fieldName,String address, String description,
                      double pricePerHour, String status, String imageUrl) {

        this.fieldId = fieldId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.fieldName = fieldName;
        this.address = address;
        this.description = description;
        this.pricePerHour = pricePerHour;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public int getFieldId() { return fieldId; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getFieldName() { return fieldName; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public double getPricePerHour() { return pricePerHour; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
}
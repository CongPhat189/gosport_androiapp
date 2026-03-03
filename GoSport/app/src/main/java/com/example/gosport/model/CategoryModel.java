package com.example.gosport.model;

public class CategoryModel {
    int id;
    String name;
    String description;

    public CategoryModel(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}

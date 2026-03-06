package com.example.gosport.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gosport.R;
import com.example.gosport.adapter.FieldAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.CategoryModel;
import com.example.gosport.model.FieldModel;

import java.util.ArrayList;

public class FieldListFragment extends Fragment {

    ListView listView;
    Spinner spCategory;

    FieldAdapter adapter;
    ArrayList<FieldModel> fieldList;
    ArrayList<CategoryModel> categoryList;

    DatabaseHelper dbHelper;

    public FieldListFragment() {
        // Required empty constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_field_list, container, false);

        listView = view.findViewById(R.id.listViewFields);
        spCategory = view.findViewById(R.id.spinnerCategory);

        dbHelper = new DatabaseHelper(getContext());

        fieldList = new ArrayList<>();
        categoryList = new ArrayList<>();


        loadFields();
        loadCategories();
        adapter = new FieldAdapter(getContext(), fieldList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {

            FieldModel selectedField = fieldList.get(position);

            Intent intent = new Intent(getActivity(), FieldDetailActivity.class);
            Log.d("est123","ok");

            intent.putExtra("fieldId", selectedField.getFieldId());
            intent.putExtra("fieldName", selectedField.getFieldName());
            intent.putExtra("categoryName", selectedField.getCategoryName());
            intent.putExtra("address", selectedField.getAddress());
            intent.putExtra("description", selectedField.getDescription());
            intent.putExtra("price", selectedField.getPricePerHour());
            intent.putExtra("status", selectedField.getStatus());
            intent.putExtra("imageUrl", selectedField.getImageUrl());

            startActivity(intent);
        });

        setupSpinner();

        return view;
    }

    private void loadFields() {

        fieldList.clear();

        Cursor cursor = dbHelper.getAllFields();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                int fieldId = cursor.getInt(0);
                int categoryId = cursor.getInt(1);
                String categoryName = cursor.getString(2);
                String fieldName = cursor.getString(3);
                String address = cursor.getString(4);
                String description = cursor.getString(5);
                double pricePerHour = cursor.getDouble(6);
                String status = cursor.getString(7);
                String imageUrl = cursor.getString(8);

                FieldModel field = new FieldModel(
                        fieldId,
                        categoryId,
                        categoryName,
                        fieldName,
                        address,
                        description,
                        pricePerHour,
                        status,
                        imageUrl
                );

                fieldList.add(field);


            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    private void loadCategories() {

        categoryList.clear();

        Cursor cursor = dbHelper.getAllCategories();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                int categoryId = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                String description = cursor.getString(2);

                CategoryModel categoryModel = new CategoryModel(
                        categoryId,
                        categoryName,
                        description
                );

                categoryList.add(categoryModel);

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    private void setupSpinner() {

        ArrayList<String> categoryNames = new ArrayList<>();
        categoryNames.add("Tất cả");

        for (CategoryModel c : categoryList) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinnerAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    adapter.filterByCategory(-1);
                }
                else{
                    int categoryId = categoryList.get(position - 1).getId();
                    adapter.filterByCategory(categoryId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });
    }


}
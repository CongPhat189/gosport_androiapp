package com.example.gosport.admin;


import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.CategoryModel;

public class CategoryManagementFragment extends Fragment {

    ListView listView;
    Button btnAdd;
    EditText edtName, edtDescription;

    ArrayList<CategoryModel> list;
    ArrayAdapter<String> adapter;

    DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category_management, container, false);

        db = new DatabaseHelper(getContext());

        listView = view.findViewById(R.id.listViewCategory);
        btnAdd = view.findViewById(R.id.btnAddCategory);
        edtName = view.findViewById(R.id.edtCategoryName);
        edtDescription = view.findViewById(R.id.edtCategoryDescription);

        loadCategories();

        btnAdd.setOnClickListener(v -> addCategory());

        listView.setOnItemClickListener((parent, view1, position, id) ->
                showUpdateDeleteDialog(list.get(position)));

        return view;
    }

    private void loadCategories() {

        list = new ArrayList<>();
        ArrayList<String> displayList = new ArrayList<>();

        Cursor cursor = db.getAllCategories();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CategoryModel category = new CategoryModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );

                list.add(category);

                // Format hiển thị đẹp hơn
                String item =
                        "Loại sân: " + category.getName() +
                                "\nMô tả: " + category.getDescription() +
                                "\n";   // thêm dòng trống cho thoáng

                displayList.add(item);

            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();

        adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                displayList
        );

        listView.setAdapter(adapter);
    }

    private void addCategory() {
        String name = edtName.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = db.insertCategory(name, desc);

        if (result > 0) {
            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
            edtName.setText("");
            edtDescription.setText("");
            loadCategories();
        } else {
            Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDeleteDialog(CategoryModel category) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cập nhật / Xóa");

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_update_category, null);

        EditText edtName = dialogView.findViewById(R.id.edtUpdateName);
        EditText edtDesc = dialogView.findViewById(R.id.edtUpdateDescription);

        edtName.setText(category.getName());
        edtDesc.setText(category.getDescription());

        builder.setView(dialogView);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = edtName.getText().toString().trim();
            String newDesc = edtDesc.getText().toString().trim();

            if (!TextUtils.isEmpty(newName)) {
                db.updateCategory(category.getId(), newName, newDesc);
                loadCategories();
            }
        });

        builder.setNegativeButton("Xóa", (dialog, which) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa danh mục này?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        db.deleteCategory(category.getId());
                        loadCategories();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        builder.setNeutralButton("Đóng", null);

        AlertDialog dialog = builder.create();
        dialog.show();


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1B5E20"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }
}
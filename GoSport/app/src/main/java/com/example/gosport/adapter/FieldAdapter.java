package com.example.gosport.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.gosport.R;
import com.example.gosport.model.FieldModel;
import com.example.gosport.user.FieldDetailActivity;

import java.util.ArrayList;
import java.text.DecimalFormat;

public class FieldAdapter extends BaseAdapter {

    Context context;
    ArrayList<FieldModel> list;
    ArrayList<FieldModel> originalList;

    public FieldAdapter(Context context, ArrayList<FieldModel> list) {
        this.context = context;
        this.list = new ArrayList<>(list);
        this.originalList = new ArrayList<>(list);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) {
        return list.get(position).getFieldId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_field, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.imgField);
        TextView name = convertView.findViewById(R.id.txtFieldName);
        TextView category = convertView.findViewById(R.id.txtCategory);
        TextView price = convertView.findViewById(R.id.txtPrice);
        TextView status = convertView.findViewById(R.id.txtStatus);
        TextView address = convertView.findViewById(R.id.txtAddress);
        DecimalFormat formatter = new DecimalFormat("#,###");
        ImageButton imageBtnDirection  = convertView.findViewById(R.id.btnDirection);
        Button btnBook = convertView.findViewById(R.id.btnBook);



        FieldModel field = list.get(position);

        name.setText(field.getFieldName());
        category.setText("Loại sân: " + field.getCategoryName());
        String priceText = formatter.format(field.getPricePerHour());
        price.setText("Giá: " + priceText + " đ/giờ");
        status.setText("Trạng thái: " + field.getStatus());
        address.setText("Địa chỉ: " + field.getAddress());
        Glide.with(context)
                .load(field.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(img);

        imageBtnDirection.setOnClickListener(v -> {

            Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(field.getAddress()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            context.startActivity(intent);
        });

        btnBook.setOnClickListener(v->{
            Intent intent = new Intent(context, FieldDetailActivity.class);
            intent.putExtra("fieldId", field.getFieldId());
            intent.putExtra("fieldName", field.getFieldName());
            intent.putExtra("categoryName", field.getCategoryName());
            intent.putExtra("address", field.getAddress());
            intent.putExtra("description", field.getDescription());
            intent.putExtra("price", field.getPricePerHour());
            intent.putExtra("status", field.getStatus());
            intent.putExtra("imageUrl", field.getImageUrl());
            context.startActivity(intent);
        });

        return convertView;
    }

    public void filterByCategory(int categoryId){
        list.clear();
        if (categoryId==-1){
            list.addAll(originalList);
        }
        else {
            for (FieldModel fieldModel:originalList){
                if (fieldModel.getCategoryId()==categoryId)
                    list.add(fieldModel);
            }
        }
        notifyDataSetChanged();
    }
    public void updateData(ArrayList<FieldModel> newList){
        this.list = newList;
        notifyDataSetChanged();
    }
}

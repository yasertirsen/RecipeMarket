package com.example.recipemarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemarket.R;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private ArrayList<String> items;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String RECIPE_ID = "RECIPE_ID";
        public static final String CALORIES = "CALORIES";
        public TextView tvItemName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView)itemView.findViewById(R.id.tvItemName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public ItemsAdapter(ArrayList<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_layout_item, parent, false);
        return new ItemsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.MyViewHolder holder, int position) {
        final String item = items.get(position);
        holder.tvItemName.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

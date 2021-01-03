package com.example.recipemarket.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemarket.R;
import com.example.recipemarket.ViewSupermarket;
import com.example.recipemarket.model.Supermarket;

import java.util.ArrayList;

import static com.example.recipemarket.fragment.MapFragment.SUPERMARKET_ID;

public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.MyViewHolder> {

    private ArrayList<Supermarket> supermarkets;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName;
        public TextView tvAddress;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            tvAddress = (TextView)itemView.findViewById(R.id.tvAddress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            Supermarket supermarket = supermarkets.get(position);

            Intent intent = new Intent(v.getContext(), ViewSupermarket.class);
            intent.putExtra(SUPERMARKET_ID, supermarket.getPlaceId());
            v.getContext().startActivity(intent);
        }
    }

    public SupermarketAdapter(ArrayList<Supermarket> supermarkets) {
        this.supermarkets = supermarkets;
    }

    @NonNull
    @Override
    public SupermarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Supermarket supermarket = supermarkets.get(position);
        holder.tvName.setText(supermarket.getName());
        holder.tvAddress.setText(supermarket.getAddress());
    }

    @Override
    public int getItemCount() {
        return supermarkets.size();
    }
}

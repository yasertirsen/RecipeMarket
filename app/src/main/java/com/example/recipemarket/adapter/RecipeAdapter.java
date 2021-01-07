package com.example.recipemarket.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemarket.R;
import com.example.recipemarket.ViewRecipe;
import com.example.recipemarket.model.Recipe;

import java.util.ArrayList;

import static com.example.recipemarket.fragment.RecipeFragment.CARBS;
import static com.example.recipemarket.fragment.RecipeFragment.FAT;
import static com.example.recipemarket.fragment.RecipeFragment.PROTEIN;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {

    private ArrayList<Recipe> recipes;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String RECIPE_ID = "RECIPE_ID";
        public static final String CALORIES = "CALORIES";
        public TextView tvTitle;
        public TextView tvCalories;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvCalories = (TextView)itemView.findViewById(R.id.tvCalories);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            Recipe recipe = recipes.get(position);
            Intent intent = new Intent(v.getContext(), ViewRecipe.class);
            intent.putExtra(RECIPE_ID, recipe.getId());
            intent.putExtra(CARBS, recipe.getCarbs());
            intent.putExtra(PROTEIN, recipe.getProtein());
            intent.putExtra(FAT, recipe.getFat());
            intent.putExtra(CALORIES, recipe.getCalories());
            v.getContext().startActivity(intent);
        }
    }

    public RecipeAdapter(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_layout_recipe, parent, false);
        return new RecipeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.MyViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvCalories.setText(recipe.getCalories());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}

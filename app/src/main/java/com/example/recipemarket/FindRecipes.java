package com.example.recipemarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipemarket.adapter.RecipeAdapter;
import com.example.recipemarket.adapter.SupermarketAdapter;
import com.example.recipemarket.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.recipemarket.fragment.RecipeFragment.CARBS;
import static com.example.recipemarket.fragment.RecipeFragment.FAT;
import static com.example.recipemarket.fragment.RecipeFragment.PROTEIN;

public class FindRecipes extends AppCompatActivity {

    private ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_recipes);

        Intent recipeIntent = getIntent();
        String carbs = recipeIntent.getStringExtra(CARBS);
        String protein = recipeIntent.getStringExtra(PROTEIN);
        String fat = recipeIntent.getStringExtra(FAT);

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.spoonacular.com/recipes/findByNutrients?maxFat=" + fat + "&maxCarbs=" + carbs + "&maxProtein=" + protein + "&number=4&apiKey=4dece4815fcb41b8991c80539cabb3a8";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray res = new JSONArray(response);
                            for(int i = 0; i < res.length(); i++) {
                                Recipe recipe = new Recipe();
                                recipe.setId(res.getJSONObject(i).getLong("id"));
                                recipe.setTitle(res.getJSONObject(i).getString("title"));
                                recipe.setCalories(res.getJSONObject(i).getInt("calories") + " calories");
                                recipe.setCarbs(res.getJSONObject(i).getString("carbs"));
                                recipe.setFat(res.getJSONObject(i).getString("fat"));
                                recipe.setProtein(res.getJSONObject(i).getString("protein"));
                                recipes.add(recipe);
                            }
                            setUpRcv();
                        } catch (JSONException e) {
                            Log.d("TAG", "Error parsing recipe");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not reach server", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    public void setUpRcv() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvRecipes);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        RecipeAdapter mAdapter = new RecipeAdapter(recipes);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
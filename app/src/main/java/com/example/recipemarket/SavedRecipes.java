package com.example.recipemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import com.example.recipemarket.adapter.RecipeAdapter;
import com.example.recipemarket.adapter.SupermarketAdapter;
import com.example.recipemarket.model.Recipe;
import com.example.recipemarket.model.Supermarket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SavedRecipes extends AppCompatActivity {

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<String> recipesIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        if(mRecyclerView == null) {
            fStore.collection("user_recipe")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                if(doc.getData().get("user_id").equals(user.getUid())) {
                                    recipesIds.add(String.valueOf(doc.getData().get("recipe_id")));
                                }
                            }
                            fStore.collection("recipes")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                                if(recipesIds.contains(doc.getId())) {
                                                    Recipe recipe = doc.toObject(Recipe.class);
                                                    recipes.add(recipe);
                                                }
                                            }
                                            setUpRcv();
                                        }
                                    });
                        }
                    });
        }
        else
            setUpRcv();

    }

    public void setUpRcv() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rvSavedRecipes);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new RecipeAdapter(recipes);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
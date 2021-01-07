package com.example.recipemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipemarket.model.Recipe;
import com.example.recipemarket.model.ShoppingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.recipemarket.Home.SHOPPING_LIST_ID;
import static com.example.recipemarket.adapter.RecipeAdapter.MyViewHolder.CALORIES;
import static com.example.recipemarket.adapter.RecipeAdapter.MyViewHolder.RECIPE_ID;
import static com.example.recipemarket.fragment.RecipeFragment.CARBS;
import static com.example.recipemarket.fragment.RecipeFragment.FAT;
import static com.example.recipemarket.fragment.RecipeFragment.PROTEIN;

public class ViewRecipe extends AppCompatActivity {
    private TextView tvRecipeTitle;
    private TextView tvCaloriesRv;
    private TextView tvCarbsG;
    private TextView tvProteinG;
    private TextView tvFatG;
    private TextView tvIngredients;
    private TextView tvInstructions;
    private Button btnAddToShopping;
    private Recipe recipe;
    ArrayList<String> ingredients;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        tvRecipeTitle = (TextView) findViewById(R.id.tvRecipeTitle);
        tvCaloriesRv = (TextView) findViewById(R.id.tvCaloriesRv);
        tvCarbsG = (TextView) findViewById(R.id.tvCarbsG);
        tvProteinG = (TextView) findViewById(R.id.tvProteinG);
        tvFatG = (TextView) findViewById(R.id.tvFatG);
        tvIngredients = (TextView) findViewById(R.id.tvIngredients);
        tvInstructions = (TextView) findViewById(R.id.tvInstructions);
        btnAddToShopping = (Button) findViewById(R.id.btnAddToShopping);

        Intent intent = getIntent();
        final long recipeId = intent.getLongExtra(RECIPE_ID, 0);
        final String carbs = intent.getStringExtra(CARBS);
        final String protein = intent.getStringExtra(PROTEIN);
        final String fat = intent.getStringExtra(FAT);
        final String calories = intent.getStringExtra(CALORIES);

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=4dece4815fcb41b8991c80539cabb3a8";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            recipe = new Recipe();
                            recipe.setId(recipeId);
                            recipe.setTitle(res.getString("title"));
                            recipe.setCalories(calories);
                            recipe.setCarbs(carbs);
                            recipe.setProtein(protein);
                            recipe.setFat(fat);
                            recipe.setInstructions(res.getString("instructions"));

                            JSONArray extendedIngredients = res.getJSONArray("extendedIngredients");
                            ingredients = new ArrayList<>();

                            for(int i = 0; i<extendedIngredients.length(); i++) {
                                ingredients.add(extendedIngredients.getJSONObject(i).getString("name"));
                            }

                            tvRecipeTitle.setText(recipe.getTitle());
                            tvCaloriesRv.setText(recipe.getCalories());
                            tvCarbsG.setText(recipe.getCarbs());
                            tvProteinG.setText(recipe.getProtein());
                            tvFatG.setText(recipe.getFat());
                            StringBuilder ingredientsSb = new StringBuilder();
                            ingredientsSb.append("Ingredients: ");
                            for(String i : ingredients) {
                                ingredientsSb.append(i).append(", ");
                            }
                            tvIngredients.setText(ingredientsSb);
                            tvInstructions.setText(Html.fromHtml(recipe.getInstructions()));
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

        btnAddToShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("shopping_lists").whereEqualTo("user_id", user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().size() > 0) {
                                        for (final DocumentSnapshot document : task.getResult()) {
                                            fStore.collection("shopping_lists").document(document.getId()).update("items", ingredients).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Ingredients added to shopping list", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), ViewShoppingList.class);
                                                    intent.putExtra(SHOPPING_LIST_ID, document.getId());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d("FTAG", "shopping list doesn't exist create a one");
                                    }
                                } else {
                                    Log.d("FTAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_supermarket_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id==R.id.action_save) {
            final Map<String, Object> userRecipe = new HashMap<>();
            Map<String, Object> recipeMap = new HashMap<>();
            if(user != null && recipe != null) {
                userRecipe.put("user_id", user.getUid());
                userRecipe.put("recipe_id", recipe.getId());

                recipeMap.put("id", recipe.getId());
                recipeMap.put("calories", recipe.getCalories());
                recipeMap.put("carbs", recipe.getCarbs());
                recipeMap.put("protein", recipe.getProtein());
                recipeMap.put("fat", recipe.getFat());
                recipeMap.put("instructions", recipe.getInstructions());
            }

            DocumentReference recipeDoc = fStore.collection("recipes").document(String.valueOf(recipe.getId()));
            recipeDoc.set(recipeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DocumentReference docRef = fStore.collection("user_recipe").document(UUID.randomUUID().toString());
                    docRef.set(userRecipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Recipe saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                        }
                    });
                }
            });
            return true;
        }
        return true;
    }
}
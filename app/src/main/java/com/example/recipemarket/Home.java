package com.example.recipemarket;

import android.content.Intent;
import android.os.Bundle;

import com.example.recipemarket.model.ShoppingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.recipemarket.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Home extends AppCompatActivity {

    public static final String SHOPPING_LIST_ID = "SHOPPING_LIST_ID";
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                fStore.collection("shopping_lists").whereEqualTo("user_id", user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().size() > 0) {
                                        for (DocumentSnapshot document : task.getResult()) {

                                            ShoppingList shoppingList = document.toObject(ShoppingList.class);
                                            if(shoppingList.getItems().isEmpty()) {
                                                Snackbar.make(view, "Shopping list is empty", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                            else {
                                                Intent intent = new Intent(getApplicationContext(), ViewShoppingList.class);
                                                intent.putExtra(SHOPPING_LIST_ID, document.getId());
                                                startActivity(intent);
                                            }
                                        }
                                    } else {
                                        Log.d("FTAG", "shopping list doesn't exist create a one");
                                        newShoppingList(view);
                                    }
                                } else {
                                    Log.d("FTAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    public void newShoppingList(View view) {
        Map<String, Object> shoppingList = new HashMap<>();
        shoppingList.put("user_id", user.getUid());
        shoppingList.put("items", new ArrayList<>());
        fStore.collection("shopping_lists").document(UUID.randomUUID().toString()).set(shoppingList);
        Snackbar.make(view, "Shopping list is empty", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
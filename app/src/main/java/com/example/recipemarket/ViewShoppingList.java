package com.example.recipemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.recipemarket.adapter.ItemsAdapter;
import com.example.recipemarket.adapter.RecipeAdapter;
import com.example.recipemarket.model.ShoppingList;
import com.example.recipemarket.model.Supermarket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.recipemarket.Home.SHOPPING_LIST_ID;
import static com.example.recipemarket.Register.TAG;
import static com.example.recipemarket.fragment.MapFragment.SUPERMARKET_ID;

public class ViewShoppingList extends AppCompatActivity {

    private ShoppingList shoppingList;
    private Button btnClear;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DocumentSnapshot doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        btnClear = (Button) findViewById(R.id.btnClear);
        Intent intent = getIntent();

        DocumentReference docRef = fStore.collection("shopping_lists").document(intent.getStringExtra(SHOPPING_LIST_ID));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    doc = task.getResult();
                    assert doc != null;
                    if(doc.exists()) {
                        shoppingList = doc.toObject(ShoppingList.class);
                        setUpRcv();
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("shopping_lists").document(doc.getId()).update("items", new ArrayList<>());
                Toast.makeText(getApplicationContext(), "Shopping list cleared", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        });
    }

    public void setUpRcv() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvShoppingList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        ItemsAdapter mAdapter = new ItemsAdapter((ArrayList<String>) shoppingList.getItems());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
package com.example.recipemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipemarket.model.Supermarket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.recipemarket.fragment.MapFragment.SUPERMARKET_ID;
import static com.example.recipemarket.fragment.MapFragment.SUPERMARKET_NAME;
import static com.example.recipemarket.Register.TAG;

public class ViewSupermarket extends AppCompatActivity {

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvOpHrs;
    private FirebaseFirestore fStore;
    private Supermarket supermarket;
    private DocumentSnapshot doc;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_supermarket);

        tvName = (TextView) findViewById(R.id.tvName);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvOpHrs = (TextView) findViewById(R.id.tvOpHrs);

        fStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        DocumentReference docRef = fStore.collection("supermarkets").document(intent.getStringExtra(SUPERMARKET_ID));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    doc = task.getResult();
                    assert doc != null;
                    if(doc.exists()) {
                        supermarket = doc.toObject(Supermarket.class);
                        tvName.setText(supermarket.getName());
                        tvAddress.setText(supermarket.getAddress());
                        StringBuilder openingHrs = new StringBuilder();
                        for(int i = 0; i<supermarket.getOpeningHours().size(); i++) {
                            openingHrs.append(supermarket.getOpeningHours().get(i)).append("\n");
                        }
                        tvOpHrs.setText(openingHrs);
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
            Map<String, String> savedSupermarket = new HashMap<>();
            if(user != null) {
                savedSupermarket.put("user_id", user.getUid());
                savedSupermarket.put("supermarket_id", doc.getId());
            }
            DocumentReference docRef = fStore.collection("user_supermarket").document(UUID.randomUUID().toString());
            docRef.set(savedSupermarket).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Supermarket saved", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return true;
    }
}
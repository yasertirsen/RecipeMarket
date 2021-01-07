package com.example.recipemarket.fragment;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipemarket.R;
import com.example.recipemarket.adapter.SupermarketAdapter;
import com.example.recipemarket.model.Supermarket;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SupermarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupermarketFragment extends Fragment {

    private View mView;
    private FirebaseFirestore fStore;
    private ArrayList<Supermarket> supermarkets = new ArrayList<>();
    private ArrayList<String> supermarketIds = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SupermarketAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SupermarketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SupermarketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SupermarketFragment newInstance(String param1, String param2) {
        SupermarketFragment fragment = new SupermarketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_supermarket, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fStore = FirebaseFirestore.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mRecyclerView == null) {
                    fStore.collection("user_supermarket")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(QueryDocumentSnapshot doc: task.getResult()) {
                                        if(doc.getData().get("user_id").equals(user.getUid())) {
                                            supermarketIds.add((String)doc.getData().get("supermarket_id"));
                                        }
                                    }
                                    fStore.collection("supermarkets")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for(QueryDocumentSnapshot doc: task.getResult()) {
                                                        if(supermarketIds.contains(doc.getId())) {
                                                            Supermarket supermarket = doc.toObject(Supermarket.class);
                                                            supermarkets.add(supermarket);
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
        }).start();

    }

    public void setUpRcv() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rvSupermarkets);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new SupermarketAdapter(supermarkets);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
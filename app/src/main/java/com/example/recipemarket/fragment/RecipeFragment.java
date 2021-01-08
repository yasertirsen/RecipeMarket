package com.example.recipemarket.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipemarket.FindRecipes;
import com.example.recipemarket.R;
import com.example.recipemarket.SavedRecipes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    public static final String CARBS = "CARBS";
    public static final String PROTEIN = "PROTEIN";
    public static final String FAT = "FAT";
    private View mView;
    private EditText etCarbs;
    private EditText etProtein;
    private EditText etFat;
    private Button btnFindRecipes;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
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
        mView = inflater.inflate(R.layout.fragment_recipe, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCarbs = (EditText) mView.findViewById(R.id.etCarbs);
        etProtein = (EditText) mView.findViewById(R.id.etProtein);
        etFat = (EditText) mView.findViewById(R.id.etFat);
        btnFindRecipes = (Button) mView.findViewById(R.id.btnFindRecipes);

        btnFindRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carbs = etCarbs.getText().toString();
                String protein = etProtein.getText().toString();
                String fat = etFat.getText().toString();
                if(carbs.matches("") || protein.matches("") || fat.matches(""))
                    Toast.makeText(getContext(), "Please fill all macros", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getContext(), FindRecipes.class);
                    intent.putExtra(CARBS, carbs);
                    intent.putExtra(PROTEIN, protein);
                    intent.putExtra(FAT, fat);
                    startActivity(intent);
                }
            }
        });

    }
}
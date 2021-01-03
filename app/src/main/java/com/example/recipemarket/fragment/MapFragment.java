package com.example.recipemarket.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipemarket.R;
import com.example.recipemarket.ViewSupermarket;
import com.example.recipemarket.model.Supermarket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.recipemarket.Register.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String SUPERMARKET_NAME = "SUPERMARKET_NAME";
    public static final String SUPERMARKET_ID = "SUPERMARKET_ID";
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private SearchView searchView;
    private Marker smMarker;
    private Supermarket supermarket;
    private FirebaseFirestore fStore= FirebaseFirestore.getInstance();;
    private String placeId;
    private List<Supermarket> supermarkets = new ArrayList<>();
    private Map<String, String> mMarkerMap = new HashMap<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        mView =  inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.mapView);
        searchView = (SearchView) mView.findViewById(R.id.svLocation);
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
                    searchApi(location.replaceAll(" ", "%20"));
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mMapView.getMapAsync(this);
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        fStore.collection("supermarkets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                Supermarket sm = document.toObject(Supermarket.class);

                                supermarkets.add(sm);
                                for(int i = 0; i < supermarkets.size(); i++) {
                                    smMarker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(sm.getLat(), sm.getLng()))
                                            .title(sm.getName()).snippet(sm.getAddress()));
                                    mMarkerMap.put(smMarker.getId(), document.getId());
                                }
                            }
                        }
                    }
                });

        CameraPosition oconnell = CameraPosition.builder().target(new LatLng(53.347265500000006, -6.259104274441249)).zoom(16).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(oconnell));

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String supermarketId = mMarkerMap.get(marker.getId());
                Intent intent = new Intent(getContext(), ViewSupermarket.class);
                intent.putExtra(SUPERMARKET_ID, supermarketId);
                startActivity(intent);

                return false;
            }
        });
    }

    public void searchApi(String keyword) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input= " + keyword + "&inputtype=textquery&fields=place_id,formatted_address" +
                "&key=AIzaSyBBmOfjyGZK0cLKq7boY1Lo4iYsNHWGZBY";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (! obj.getString("status").equals("OK"))
                                return;
                            JSONObject res = obj.getJSONArray("candidates").getJSONObject(0);
                            placeId = res.getString("place_id");

                            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&key=AIzaSyBBmOfjyGZK0cLKq7boY1Lo4iYsNHWGZBY";

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (! obj.getString("status").equals("OK"))
                                                    return;
                                                JSONObject res = obj.getJSONObject("result");
                                                JSONObject loc = res.getJSONObject("geometry").getJSONObject("location");
                                                LatLng location = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                                                JSONArray opHrs = res.getJSONObject("opening_hours").getJSONArray("weekday_text");
                                                List<String> openingHours = new ArrayList<>();

                                                for(int i = 0; i<opHrs.length(); i++) {
                                                    openingHours.add(opHrs.getString(i));
                                                }

                                                supermarket = new Supermarket(placeId,
                                                        res.getString("name"),
                                                        res.getString("formatted_address"),
                                                        location.latitude,
                                                        location.longitude,
                                                        res.getString("website"),
                                                        openingHours,
                                                        res.getDouble("rating"));
                                                saveSupermarket();
                                                mGoogleMap.addMarker(new MarkerOptions().position(location).title(res.getString("name")).snippet(res.getString("formatted_address")));
                                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Error getting place details", Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(stringRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error getting place", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    public void saveSupermarket() {
        Map<String, Object> supermarketMap = new HashMap<>();
        supermarketMap.put("name", supermarket.getName());
        supermarketMap.put("address", supermarket.getAddress());
        supermarketMap.put("lat", supermarket.getLat());
        supermarketMap.put("lng", supermarket.getLng());
        supermarketMap.put("website", supermarket.getWebsite());
        supermarketMap.put("openingHours", supermarket.getOpeningHours());
        supermarketMap.put("rating", supermarket.getRating());
        DocumentReference documentReference = fStore.collection("supermarkets")
                .document(supermarket.getPlaceId());
        documentReference.set(supermarketMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Supermarket added " + supermarket.getName());
            }
        });


    }
}
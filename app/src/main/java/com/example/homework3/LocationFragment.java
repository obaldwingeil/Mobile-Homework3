package com.example.homework3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LocationFragment extends Fragment {
    View view;
    private Context context;

    private ArrayList<Location> locationList;
    private RecyclerView recyclerView;

    private static final String api = "https://rickandmortyapi.com/api/location";
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_location);
        locationList = new ArrayList<>();

        context = view.getContext();

        getApi();

        return view;
    }
    public void getApi(){
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Location api", new String(responseBody));
                try {
                    JSONObject data = new JSONObject(new String(responseBody));
                    JSONArray array = data.getJSONArray("results");
                    for(int i = 0; i < array.length(); i++){
                        Location location = new Location(
                                array.getJSONObject(i).getString("name"),
                                array.getJSONObject(i).getString("type"),
                                array.getJSONObject(i).getString("dimension")
                        );
                        locationList.add(location);
                    }
                    LocationAdapter adapter = new LocationAdapter(locationList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}

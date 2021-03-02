package com.example.homework3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class CharacterFragment extends Fragment {

    View view;

    ImageView imageView_character;
    TextView textView_name;
    TextView textView_status;
    TextView textView_species;
    TextView textView_gender;
    TextView textView_origin;
    TextView textView_location;
    TextView textView_episodes;

    private static final String  api = "https://rickandmortyapi.com/api/character";
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_character, container, false);

        imageView_character = view.findViewById(R.id.imageView_character);
        textView_name = view.findViewById(R.id.textView_name);
        textView_status = view.findViewById(R.id.textView_status);
        textView_species = view.findViewById(R.id.textView_species);
        textView_gender = view.findViewById(R.id.textView_gender);
        textView_origin = view.findViewById(R.id.textView_origin);
        textView_location = view.findViewById(R.id.textView_location);
        textView_episodes = view.findViewById(R.id.textView_episodes);

        apiCall();

        return view;
    }

    public void apiCall(){
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api", new String(responseBody));
                try {
                    JSONObject data = new JSONObject(new String(responseBody));
                    int count = data.getJSONObject("info").getInt("count");
                    // Log.d("count", String.valueOf(count));
                    Random r = new Random();
                    int id = r.nextInt(count + 1);
                    Log.d("id", String.valueOf(id));
                    getCharacter(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getCharacter(int id){
        client.get(api + "/" + id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("character api response", new String(responseBody));
                try {
                    JSONObject character = new JSONObject(new String(responseBody));
                    Picasso.get().load(character.getString("image")).into(imageView_character);
                    textView_name.setText(character.getString("name"));
                    textView_status.setText("Status: " + character.getString("status"));
                    textView_species.setText("Species: " + character.getString("species"));
                    textView_gender.setText("Gender: " + character.getString("gender"));
                    textView_origin.setText("Origin: " + character.getJSONObject("origin").getString("name"));
                    textView_location.setText("Location: " + character.getJSONObject("location").getString("name"));
                    JSONArray episodes = character.getJSONArray("episode");
                    String episodes_str = "";
                    for(int i = 0; i < episodes.length(); i++){
                        String num = episodes.getString(i).substring(episodes.getString(i).length()-3);
                        if(num.charAt(0) == '/'){
                            num = num.substring(1);
                        }
                        else if(num.charAt(0) == 'e'){
                            num = num.substring(2);
                        }
                        episodes_str += num;
                        if(i != episodes.length()-1){
                            episodes_str += ", ";
                        }
                    }
                    textView_episodes.setText("Episodes: " + episodes_str);
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

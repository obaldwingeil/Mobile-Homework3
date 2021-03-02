package com.example.homework3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class EpisodeFragment extends Fragment {
    View view;

    ImageView imageView_episodeChar1;
    ImageView imageView_episodeChar2;
    ImageView imageView_episodeChar3;
    TextView textView_episodeName;
    TextView textView_airDate;
    Button button_more;

    private Context context;

    private static final String  api = "https://rickandmortyapi.com/api/episode";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private String number;
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_episode, container, false);

        context = view.getContext();
        imageView_episodeChar1 = view.findViewById(R.id.imageView_episodeChar1);
        imageView_episodeChar2 = view.findViewById(R.id.imageView_episodeChar2);
        imageView_episodeChar3 = view.findViewById(R.id.imageView_episodeChar3);
        textView_episodeName = view.findViewById(R.id.textView_episodeName);
        textView_airDate = view.findViewById(R.id.textView_airDate);

        button_more = view.findViewById(R.id.button_moreInfo);
        button_more.setOnClickListener(v -> {
            int NOTIFICATION_ID = 234;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "my_channel_01";
                CharSequence name = "my_channel";
                String Description = "This is my channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_01")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(number + ": " + name)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("To read more information about Episode " + number + ", please visit: https://rickandmorty.fandom.com/wiki/" + name.replace(' ', '_')));

            String url = "https://rickandmorty.fandom.com/wiki/" + name.replace(' ', '_');
            Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Log.d("activity", resultIntent.resolveActivity(context.getPackageManager()).toString());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        });

        apiCall();

        return view;
    }

    public void apiCall(){
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Episode api", new String(responseBody));
                try {
                    JSONObject data = new JSONObject(new String(responseBody));
                    int count = data.getJSONObject("info").getInt("count");
                    // Log.d("count", String.valueOf(count));
                    Random r = new Random();
                    int id = r.nextInt(count + 1);
                    Log.d("id", String.valueOf(id));
                    getEpisode(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getEpisode(int id){
        client.get(api + "/" + id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("episode api response", new String(responseBody));
                try {
                    JSONObject episode = new JSONObject(new String(responseBody));
                    textView_episodeName.setText(episode.getString("episode") + " " + episode.getString("name"));
                    textView_airDate.setText("Aired on: " + episode.getString("air_date"));
                    name = episode.getString("name");
                    number = episode.getString("episode");
                    JSONArray characters = episode.getJSONArray("characters");
                    int place = 0;
                    ArrayList<String> charArray = new ArrayList<>();
                    for(int i = 0; i < characters.length(); i++){
                        place++;
                        if(place > 3){
                            break;
                        }
                        String charID = characters.getString(i).substring(characters.getString(i).length()-3);
                        if(charID.charAt(0) == '/'){
                            charID = charID.substring(1);
                        }
                        else if(charID.charAt(0) == 'r'){
                            charID = charID.substring(2);
                        }
                        else if(charID.charAt(0) == 'e'){
                            charID = charID.substring(3);
                        }
                        charArray.add(charID);
                    }
                    if(charArray.size() == 1){
                        getCharacterImage(Integer.parseInt(charArray.get(0)), 1);
                    }
                    else if(charArray.size() == 2){
                        getCharacterImage(Integer.parseInt(charArray.get(0)), 1);
                        getCharacterImage(Integer.parseInt(charArray.get(1)), 2);
                    }
                    else{
                        getCharacterImage(Integer.parseInt(charArray.get(0)), 1);
                        getCharacterImage(Integer.parseInt(charArray.get(1)), 2);
                        getCharacterImage(Integer.parseInt(charArray.get(2)), 3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getCharacterImage(int id, int num){
        String charApi = "https://rickandmortyapi.com/api/character";
        client.get(charApi + "/" + id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("character api response", new String(responseBody));
                try {
                    JSONObject character = new JSONObject(new String(responseBody));
                    String image = character.getString("image");
                    if(num == 1){
                        Picasso.get().load(image).into(imageView_episodeChar1);
                    }
                    else if(num == 2){
                        Picasso.get().load(image).into(imageView_episodeChar2);
                    }
                    else if(num == 3){
                        Picasso.get().load(image).into(imageView_episodeChar3);
                    }
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

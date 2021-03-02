package com.example.homework3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView imageView_logo;
    Button button_character;
    Button button_episode;
    Button button_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView_logo = findViewById(R.id.imageView_logo);
        Picasso.get().load("file:///android_asset/logo.png").into(imageView_logo);

        button_character = findViewById(R.id.button_character);
        button_character.setOnClickListener(v -> {
            loadFragment(new CharacterFragment(), R.id.fragContainer_main);
        });

        button_location = findViewById(R.id.button_location);
        button_location.setOnClickListener(v -> {
            loadFragment(new LocationFragment(), R.id.fragContainer_main);
        });

        button_episode = findViewById(R.id.button_episode);
        button_episode.setOnClickListener(v -> {
            loadFragment(new EpisodeFragment(), R.id.fragContainer_main);
        });

    }

    public void loadFragment(Fragment fragment, int id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create a fragment transaction to begin the transaction and replace the fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //replacing the placeholder - fragmentContainterView with the fragment that is passed as parameter
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }
}
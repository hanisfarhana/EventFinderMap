package com.example.eventfindermap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the AboutActivity and return to the previous activity
            }
        });

        TextView openUrlText = findViewById(R.id.app_url);
        openUrlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage();
            }
        });
    }

    private void openWebPage() {
        String url = "https://github.com/hanisfarhana/EventFinderMap";

        // Create a Uri object from the URL
        Uri webpage = Uri.parse(url);

        // Create an Intent with action view and the Uri
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Start the activity without checking if there's an app to handle it
        startActivity(intent);
    }
}

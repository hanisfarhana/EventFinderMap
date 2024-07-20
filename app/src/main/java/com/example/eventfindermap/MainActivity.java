package com.example.eventfindermap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private GoogleMap mMap;
    private Retrofit retrofit;
    private EventApi eventApi;
    private HashMap<Marker, Event> markerEventMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-eventfindermap.cloudfunctions.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        eventApi = retrofit.create(EventApi.class);
        Log.d(TAG, "Retrofit initialized");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "Google Map is ready");
        // Center the map on Kuala Lumpur, Malaysia
        LatLng malaysia = new LatLng(3.1390, 101.6869);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 10f));
        fetchEvents();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event event = markerEventMap.get(marker);
                if (event != null) {
                    // Display event information
                    Log.d(TAG, "Clicked marker for event: " + event.name);
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle(event.name)
                            .setMessage("Type: " + event.type + "\nSchedule: " + event.schedule + "\nTickets: " + event.ticketInfo)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                return false;
            }
        });
    }

    private void fetchEvents() {
        Log.d(TAG, "Fetching events");
        eventApi.getEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    List<Event> events = response.body();
                    Log.d(TAG, "Events fetched successfully, count: " + (events != null ? events.size() : 0));
                    if (events != null) {
                        for (Event event : events) {
                            LatLng location = new LatLng(event.latitude, event.longitude);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(event.name));
                            markerEventMap.put(marker, event);
                            Log.d(TAG, "Added marker for event: " + event.name);
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to fetch events, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e(TAG, "Error fetching events", t);
            }
        });
    }

    interface EventApi {
        @GET("events")
        Call<List<Event>> getEvents();

        @POST("events")
        Call<Void> addEvent(@Body Event event);
    }

    static class Event {
        @SerializedName("name")
        String name;

        @SerializedName("type")
        String type;

        @SerializedName("latitude")
        double latitude;

        @SerializedName("longitude")
        double longitude;

        @SerializedName("schedule")
        String schedule;

        @SerializedName("ticketInfo")
        String ticketInfo;
    }
}

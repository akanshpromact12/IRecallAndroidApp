package com.promact.akansh.irecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by Akansh on 28-07-2017.
 */

public class viewPostsActivity extends AppCompatActivity implements /*NavigationView.OnNavigationItemSelectedListener, */GoogleApiClient.OnConnectionFailedListener {
    public static String TAG="viewPostActivity";
    ArrayList<AlbumDetails> images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_albums_main);

        String latLng;
        Intent intent = getIntent();

        latLng = intent.getStringExtra("lat");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewPost);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new
                GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        images = (ArrayList<AlbumDetails>) intent.getSerializableExtra("listOfImages");

        for (int i=0; i<images.size(); i++) {
            Log.d(TAG, "images: "+images.get(i).caption);
        }

        ImageAdapter adapter = new ImageAdapter(viewPostsActivity.this, images, latLng);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection unsuccessful");
    }
}

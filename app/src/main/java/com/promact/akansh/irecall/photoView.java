package com.promact.akansh.irecall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Akansh on 04-08-2017.
 */

public class photoView extends AppCompatActivity {
    String mediaUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewPost);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        ImageView fullScreenImage = (ImageView) findViewById(R.id.fullPageImage);
        VideoView videoView = (VideoView) findViewById(R.id.video_view);

        Intent intent = getIntent();
        mediaUrl = intent.getStringExtra("media");

        if (mediaUrl.contains(".mp4")) {
            fullScreenImage.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + mediaUrl + "?alt=media&token=1");
            videoView.setVideoURI(uri);
            videoView.setMediaController(new MediaController(this));
            videoView.requestFocus();
            videoView.start();
        } else {
            videoView.setVisibility(View.GONE);
            fullScreenImage.setVisibility(View.VISIBLE);

            Glide.with(photoView.this)
                    .load("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + mediaUrl + "?alt=media&token=1")
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (!isFromMemoryCache) {
                                resource.start();
                            }

                            return false;
                        }
                    })
                    .into(fullScreenImage);
        }
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
}

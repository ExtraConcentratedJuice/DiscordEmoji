package com.extraconcentratedjuice.discordemoji;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class EmojiDetails extends AppCompatActivity {

    public String author;
    public String title;
    public String description;
    public String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_details);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        description = intent.getStringExtra("description");
        slug = intent.getStringExtra("slug");
        setTitle(title);
        TextView titletext = findViewById(R.id.name);
        TextView authortext = findViewById(R.id.author);
        TextView descriptiontext = findViewById(R.id.description);
        ImageView img = findViewById(R.id.emojiimage);
        Picasso.with(this).load(HTTP.ASSET_URL + slug + ".png").into(img);
        titletext.setText(title);
        authortext.setText("By: " + author);
        descriptiontext.setText(description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.download:
                String url = HTTP.ASSET_URL + slug + ".png";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription(description);
                request.setTitle(title);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/DiscordEmoji", title + ".png");
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

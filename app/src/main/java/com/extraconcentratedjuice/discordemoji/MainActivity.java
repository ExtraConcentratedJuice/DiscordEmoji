package com.extraconcentratedjuice.discordemoji;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public ExpandableListView view;
    public ExpandableListAdapter expandableListAdapter;
    public List<String> expandableListTitle;
    public HashMap<String, List<Emoji>> details;
    private ProgressDialog loading;
    private RelativeLayout layout;
    private Long lastUpdated;
    private Toast toast;

    // fix for indexoutofbounds exception on list update
    private List<String> tempTitle;
    private HashMap<String, List<Emoji>> tempDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        lastUpdated = System.currentTimeMillis();
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);
        expandableListTitle = new ArrayList<String>();
        details = new HashMap<>();
        layout = findViewById(R.id.activity_main);
        loading = new ProgressDialog(this);
        loading.setTitle("Loading...");
        loading.setMessage("Please wait...");
        loading.show();

        new Initialize().execute();
    }

    class Initialize extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... urls) {
            JSONObject data;
            tempDetail = new HashMap<>();
            tempTitle = new ArrayList<>();
            try {
                data = HTTP.getCategories();
            }
            catch (IOException e)
            {
                try
                {
                    // why create a toast when you can make a fake listview? lol
                    return new JSONObject("{\"0\": \"Request to DiscordEmoji.com failed.\", \"1\": \"Do you have a working network connection?\"}");
                }
                catch (org.json.JSONException ex)
                {
                    return new JSONObject();
                }

            }
            return data;
        }

        protected void onPostExecute(JSONObject json) {
            Iterator<?> i = json.keys();
            while (i.hasNext()) {
                String key = (String) i.next();
                try {
                    String name = json.getString(key);
                    tempTitle.add(name);
                    Emoji.categories.put(Integer.parseInt(key), name);
                    tempDetail.put(json.getString(key), new ArrayList<Emoji>());

                } catch (org.json.JSONException e) {
                    System.out.println(e.toString());
                }
            }
            new GetEmojiData().execute();
        }
    }

    class GetEmojiData extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... meme) {

            JSONArray data;
            try {
                data = HTTP.getAllEmoji();
            }
            catch (IOException e)
            {
                return new JSONArray();
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject o = data.getJSONObject(i);
                    int id = o.getInt("id");
                    String title = o.getString("title");
                    String description = o.getString("description");
                    String slug = o.getString("slug");
                    int emojiCategory = o.getInt("category");
                    String author = o.getString("submitted_by");

                    Emoji emoji = new Emoji(id, title, slug, description, emojiCategory, author);
                    tempDetail.get(emoji.CategoryName()).add(emoji);

                } catch (org.json.JSONException ex) {
                    System.out.println(ex.toString());
                }
            }
            return data;
        }

        protected void onPostExecute(JSONArray json) {
            if (expandableListAdapter == null)
            {
                expandableListTitle = new ArrayList<>(tempTitle);
                details = new HashMap<>(tempDetail);
                expandableListAdapter = new CustomExpandableListAdapter(MainActivity.this, expandableListTitle, details);
                view.setAdapter(expandableListAdapter);
            }
            else
            {
                expandableListTitle.clear();
                details.clear();
                expandableListTitle = new ArrayList<>(tempTitle);
                details = new HashMap<>(tempDetail);
                // notifyDataSetChanged generates a blank listview for some reason, creating new listadapter instead
                expandableListAdapter = new CustomExpandableListAdapter(MainActivity.this, expandableListTitle, details);
                view.setAdapter(expandableListAdapter);
            }

            MainActivity.instance.loading.dismiss();


            view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Emoji emote = (Emoji) expandableListAdapter.getChild(groupPosition, childPosition);
                    Intent intent = new Intent(MainActivity.this, EmojiDetails.class);
                    intent.putExtra("title", emote.title);
                    intent.putExtra("author", emote.author);
                    intent.putExtra("description", emote.description);
                    intent.putExtra("slug", emote.slug);
                    intent.putExtra("category", emote.CategoryName());
                    return true;
                }
            });

        }
    }


    public void openInfo()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_info_black_24dp);
        dialog.setTitle("Information");
        TextView tx = new TextView(this);
        tx.setPadding(35, 15, 35, 15);
        tx.setText(R.string.about);
        tx.setTextColor(Color.BLACK);
        tx.setTextSize(15);
        tx.setMovementMethod(LinkMovementMethod.getInstance());
        tx.setAutoLinkMask(RESULT_OK);
        Linkify.addLinks(tx, Linkify.WEB_URLS);
        dialog.setView(tx);

        dialog.setPositiveButton("Dismiss", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                openInfo();
            case R.id.reload:
                if (System.currentTimeMillis() - lastUpdated > 1500)
                {
                    lastUpdated = System.currentTimeMillis();
                    new Initialize().execute();
                }
                else
                {
                    final String text = "You are refreshing too fast!";
                    int duration = Toast.LENGTH_SHORT;
                    if (toast != null)
                    {
                        toast.getView().isShown();
                        toast.setText(text);
                    }
                    else
                    {
                        toast = Toast.makeText(this, text, duration);
                    }
                    toast.show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

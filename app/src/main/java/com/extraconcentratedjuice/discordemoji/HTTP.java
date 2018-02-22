package com.extraconcentratedjuice.discordemoji;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HTTP {
    public static final String BASE_URL =  "https://discordemoji.com/api";
    public static final String ASSET_URL = "https://discordemoji.com/assets/emoji/";

    public static String getPage(String url) throws java.io.IOException {
        URL httpurl = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) httpurl.openConnection();
        urlConnection.connect();
        InputStream in = urlConnection.getInputStream();
        String out = readStream(in);
        return out;
    }

    public static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static JSONObject getCategories() throws java.io.IOException
    {
        String data = getPage(BASE_URL + "?request=categories");
        try
        {
            return new JSONObject(data);
        }
        catch (org.json.JSONException e)
        {
            System.out.println(e.toString());
            return null;
        }
    }

    public static JSONArray getAllEmoji() throws java.io.IOException
    {
        String data = getPage(BASE_URL);
        try
        {
            return new JSONArray(data);
        }
        catch (org.json.JSONException e)
        {
            System.out.println(e.toString());
            return null;
        }
    }

    public static JSONArray searchEmoji(String query) throws java.io.IOException {
        String data = getPage(BASE_URL + "?request=search&q=" + URLEncoder.encode(query, "UTF8"));
        try
        {
            return new JSONArray(data);
        }
        catch (org.json.JSONException e)
        {
            System.out.println(e.toString());
            return null;
        }
    }
}


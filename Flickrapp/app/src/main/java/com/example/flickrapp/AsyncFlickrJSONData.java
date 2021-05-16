package com.example.flickrapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class AsyncFlickrJSONData extends AsyncTask<String, Void, JSONObject> {
    private JSONObject jsonObject;
    @SuppressLint("StaticFieldLeak")
    private final MainActivity activity;

    public AsyncFlickrJSONData(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        URL url = null;
        Log.i("TMM", "ASYNC start");

        try {
            url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);
                s = s.substring("jsonFlickrFeed(".length(), s.length() - 1);
                jsonObject = new JSONObject(s);
                Log.i("TMM", jsonObject.toString());
            } finally {
                urlConnection.disconnect();
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.i("TMM", jsonObject.toString());
        try {
            String image_url = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("media").getString("m");
            Log.i("TMM", image_url);

            activity.setImageURL(image_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        String line;

        for (line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}

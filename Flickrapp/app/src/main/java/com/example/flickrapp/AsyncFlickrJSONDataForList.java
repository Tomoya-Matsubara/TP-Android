package com.example.flickrapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

public class AsyncFlickrJSONDataForList extends AsyncTask<String, Void, JSONObject> {
    private JSONObject jsonObject;
    private ListActivity.MyAdapter adapter;
    private boolean GPSflag;

    public AsyncFlickrJSONDataForList(ListActivity.MyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        GPSflag = params[1].equals("GPS");
        URL url = null;

        try {
            url = new URL(params[0]);

            Log.i("TMM", "URL (Async): " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);

                if (!GPSflag) {
                    s = s.substring("jsonFlickrFeed(".length(), s.length() - 1);
                } else {
                    s = s.substring("jsonFlickrApi(".length(), s.length() - 1);
                }

                jsonObject = new JSONObject(s);
                Log.i("TMM", "JSON: " + jsonObject.toString());
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

        if (!GPSflag) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    String image_url = items.getJSONObject(i).getJSONObject("media").getString("m");
                    Log.i("TMM", "Adding to adapter url: " + image_url);
                    adapter.dd(image_url);
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray photo = jsonObject.getJSONObject("photos").getJSONArray("photo");

                for (int i = 0; i < photo.length(); i++) {
                    String farm_id = photo.getJSONObject(i).getString("farm");
                    String server_id = photo.getJSONObject(i).getString("server");
                    String id = photo.getJSONObject(i).getString("id");
                    String secret = photo.getJSONObject(i).getString("secret");

                    String image_url = "https://farm" + farm_id + ".staticflickr.com/" + server_id
                            + "/" + id + "_" + secret + ".jpg";
                    Log.i("TMM", "Adding to adapter url: " + image_url);
                    adapter.dd(image_url);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

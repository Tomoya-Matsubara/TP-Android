package com.example.flickrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.Vector;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        MyAdapter adapter = new MyAdapter();
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        AsyncFlickrJSONDataForList task = new AsyncFlickrJSONDataForList(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String url;

        if (prefs.getBoolean("gps_switch", false)) {
            Bundle extras = getIntent().getExtras();
            String lat = new String(extras.getString("lat"));
            String lon = new String(extras.getString("lon"));

            Log.i("TMM", "Lat: " + lat + " Lon: " + lon);

            url = "https://api.flickr.com/services/rest/?"
                    + "method=flickr.photos.search&license=4&api_key=e2c60662e4c7bf97683b65e5445a7243"
                    + "&has_geo=1&lat=" + lat + "&lon=" + lon + "&format=json";

            task.execute(url, "GPS");

            Log.i("TMM", "(GPS) URL: " + url);
        } else {
            String tag = prefs.getString("tag", "trees");
            url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=" + tag + "&format=json";

            task.execute(url, "Normal");
        }

//        if (prefs.getBoolean("gps_switch", false)) {
//            Bundle extras = getIntent().getExtras();
//            String lat = extras.getString("lat");
//            String lon = extras.getString("lon");
//
//            url = "https://api.flickr.com/services/rest/?"
//                    + "method=flickr.photos.search&license=4&api_key=e2c60662e4c7bf97683b65e5445a7243"
//                    + "&has_geo=1&lat=" + lat + "&lon=" + lon + "&per_page=1&format=json";
//
//            Log.i("TMM", "Lat: " + lat + " Lon: " + lon);
//            Log.i("TMM", "(GPS) URL: " + url);
//        }
//
//        String tag = prefs.getString("tag", "tree");
//        url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=" + tag + "s&format=json";


        Log.i("TMM", "URL: " + url);
    }

    public class MyAdapter extends BaseAdapter {
        Vector<String> vector;

        public MyAdapter() {
            vector = new Vector<>();
        }

        @Override
        public int getCount() {
            return vector.size();
        }

        @Override
        public Object getItem(int position) {
            return vector.elementAt(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textviewarray, parent, false);
//            }
//
//            String url = getItem(position).toString();
//            TextView textView = (TextView) convertView.findViewById(R.id.url_textView) ;
//            textView.setText(url);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bitmaplayout, parent, false);
            }

            String url = getItem(position).toString();
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            Response.Listener<Bitmap> rep_listener = imageView::setImageBitmap;

            Response.ErrorListener err_listener = (VolleyError error) -> {
                Log.i("TMM", error.getMessage());
            };

            ImageRequest imageRequest = new ImageRequest(url, rep_listener, 0, 0,
                    ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, err_listener);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean cache = prefs.getBoolean("cache_switch", true);
            if (!cache) {
                imageRequest.setShouldCache(false);
            }

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);

            return convertView;
        }

        public void dd(String url) {
            this.vector.add(url);
        }

    }
}
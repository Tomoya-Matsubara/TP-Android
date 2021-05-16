package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Authentication extends AppCompatActivity {
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Get widgets
        Button login_button = (Button) findViewById(R.id.authentification_button);
        EditText login = (EditText)findViewById(R.id.login);
        EditText password = (EditText) findViewById(R.id.password);
        EditText output = (EditText) findViewById(R.id.result);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL("https://httpbin.org/basic-auth/bob/sympa");    // URL definition
                            String login_str = login.getText().toString();                      // Get Login input
                            String pass_str = password.getText().toString();                    // Get Password input
                            String message = login_str + ":" + pass_str;

                            Log.i("TMM", message);
                            String basicAuth = "Basic " +
                                    Base64.encodeToString(message.getBytes(), Base64.NO_WRAP);

                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty("Authorization", basicAuth);

                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                String s = readStream(in);
                                Log.i("TMM", s);

                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    res = jsonObject.getString("authenticated");

                                    Log.i("TMM", res);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            output.setText(res);
                                        }
                                    });
                                } catch (JSONException err) {
                                    Log.i("TMM", "Exception : "+err.toString());
                                }
                            } finally {
                                urlConnection.disconnect();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
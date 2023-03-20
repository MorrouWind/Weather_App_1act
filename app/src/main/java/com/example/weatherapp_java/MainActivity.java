package com.example.weatherapp_java;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result_info;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);


        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, R.string.text_null,Toast.LENGTH_LONG).show();
                }else {
                    String city = user_field.getText().toString();
                    String key = "191f78a8ca631cc01fe3dd0eb34f8534";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city  + "&units=metric&lang=ua&appid=" + key;

                     new GetUrlData().execute(url);
                }
            }
        });
    }


    private class GetUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            result_info.setText("Очікування відповіді від серверу...");
        }


        @SuppressLint("SuspiciousIndentation")
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line=reader.readLine()) !=null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (connection != null)
                    connection.disconnect();

                    try {
                        if (reader != null)
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return "Упс... Щось пішло не так...";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject object = new JSONObject(result);
                JSONObject main = object.getJSONObject("main");

               // result_info.setText("Температура: " + object.getJSONObject("main").getDouble("temp"));

                result_info.setText("Місто: " + object.getString("name") + "\n"
                        + "Температура: " + main.getDouble("temp") + " C°" + "\n"
                        + "Хмарність: " + main.getDouble("humidity") + " %" + "\n"
                //        + "http://openweathermap.org/img/wn/" + main.getString("icon") + "@2x.png"
                );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
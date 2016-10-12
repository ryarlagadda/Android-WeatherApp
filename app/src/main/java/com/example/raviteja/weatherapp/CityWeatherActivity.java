package com.example.raviteja.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.solidfire.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CityWeatherActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    WeatherAdapter adapter;
    ListView listView;
    int m=0;
    String CityName,StateName,Temperature,curr_Date;
    ArrayList<Weather> weathersList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("City Weather");
        setContentView(R.layout.activity_city_weather);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.view2);
        setSupportActionBar(mtoolbar);
        if (getIntent().getExtras() !=null) {
            Bundle b = getIntent().getExtras();
            CityName=b.get("CityName").toString();
            StateName = b.get("StateName").toString();
            CityName=CityName.replace(" ","_");
            Log.d("city",CityName);
            new GetData().execute("http://api.wunderground.com/api/1a18786d2fa231d1/hourly/q/"+StateName+"/"+CityName+".json");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.custom_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_setting)
        {
            Boolean flag=true;
            SharedPreferences mPrefs=getSharedPreferences("Favorites", Context.MODE_PRIVATE);
           SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(weathersList.get(0)); // myObject - instance of MyObject
            while(flag)
            {
                if(mPrefs.contains("MyObject"+m))
                {

                    String json2 = mPrefs.getString("MyObject"+m, "");
                    Weather obj = gson.fromJson(json2, Weather.class);
                    if((obj.getCityName()).equals(weathersList.get(0).getCityName())&&(obj.getStateName()).equals(weathersList.get(0).getStateName()))
                    {
                        Log.d("matched","MyObject"+m);
                        prefsEditor.remove("MyObject"+m);
                        prefsEditor.putString("MyObject"+m,json);
                        prefsEditor.commit();
                        flag=false;
                        Toast.makeText(CityWeatherActivity.this, "Updated to Favorites", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        m++;
                    }
                }
                else{
                    prefsEditor.putString("MyObject"+m,json);
                    prefsEditor.commit();
                    flag=false;
                    Toast.makeText(CityWeatherActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    class GetData extends AsyncTask<String,Void,ArrayList<Weather>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CityWeatherActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading Hourly Data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Weather> doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Log.d("inside doback","check");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                Log.d("inside doback","coneected");
                int statuscode = con.getResponseCode();
                if(statuscode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = reader.readLine();
                    while(line!=null)
                    {
                        sb.append(line);
                        Log.d("String",line);
                        line = reader.readLine();
                    }

                    return WeatherUtil.PullWeather.parseWeather(sb.toString(),CityName, StateName);
                }
            } catch (MalformedURLException e) {
                Log.d("url","wrong");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(final ArrayList<Weather> weathers) {
            if (weathers == null) {
                Intent intent = new Intent(CityWeatherActivity.this,MainActivity.class);
                intent.putExtra("Error","Error");
                startActivity(intent);
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Log.d("check", weathers.get(0).getClouds());
                weathersList=weathers;
                Temperature= weathers.get(0).getTemperature()+"°F";
                String[] time=weathers.get(0).getTime().split("-");
                curr_Date=time[0];
                TextView current_city = (TextView) findViewById(R.id.current_location);
                current_city.setText("Current Location: " + CityName + "," + StateName);
                listView = (ListView) findViewById(R.id.weather_list);
                adapter = new WeatherAdapter(CityWeatherActivity.this, R.layout.row_layout, weathers);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(CityWeatherActivity.this,DetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("KEY", weathers.get(i));
                        intent.putExtra("loc","Current Location: " + CityName + "," + StateName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                super.onPostExecute(weathers);
            }
        }
    }






    }

package com.example.raviteja.weatherapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.solidfire.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Weather App");
        setContentView(R.layout.activity_main);
        final EditText cityName = (EditText) findViewById(R.id.CityName);
        final EditText stateName = (EditText) findViewById(R.id.StateName);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.view2);
        setSupportActionBar(mtoolbar);
        Button submit = (Button) findViewById(R.id.Submit_button);
       final TextView nofav = (TextView) findViewById(R.id.no_favorites_mess_tv);
        final ListView listView = (ListView) findViewById(R.id.listView);
        ArrayList<Weather> weathers= new ArrayList<Weather>();
        Weather weather=new Weather();
        final ArrayList<Weather> favorites = new ArrayList<>();;
        int i=0;
        Boolean flag=true;
        SharedPreferences sharedPreferences=getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        while(flag) {
            if (sharedPreferences.contains("MyObject"+i)) {

                Gson gson = new Gson();
                String json = sharedPreferences.getString("MyObject"+i, "");
                Weather obj = gson.fromJson(json, Weather.class);
                favorites.add(obj);
                i++;

            } else {
                flag=false;
            }
        }
        if(favorites.size()==0)
        {
            nofav.setText("There is no city in your Favorites");
        }
        else
        {
            FavoriteAdapter adapter = new FavoriteAdapter(MainActivity.this, R.layout.favorite_row_layout, favorites);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,CityWeatherActivity.class);
                intent.putExtra("CityName", favorites.get(i).CityName);
                intent.putExtra("StateName", favorites.get(i).StateName);
                Log.d("pass","done");
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView = (ListView) findViewById(R.id.listView);
                Log.d("removed",favorites.get(i).getCityName());
                favorites.remove(i);
                SharedPreferences sharedPreferences=getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("MyObject"+i);
                editor.commit();
                Log.d("position",Integer.toString(i));
                FavoriteAdapter adapter = new FavoriteAdapter(MainActivity.this, R.layout.favorite_row_layout, favorites);
                listView.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "City Deleted", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CityWeatherActivity.class);
                intent.putExtra("CityName", cityName.getText());
                intent.putExtra("StateName", stateName.getText());
                Log.d("moved","done");
                startActivity(intent);
            }
        });
        if (getIntent().getExtras()!=null) {
            Toast.makeText(MainActivity.this,"No cities match your query", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.custom_menu,menu);
        return true;
    }
}

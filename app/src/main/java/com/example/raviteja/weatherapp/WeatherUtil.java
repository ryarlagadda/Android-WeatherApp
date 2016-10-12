package com.example.raviteja.weatherapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by RAVITEJA on 10/5/2016.
 */

public class WeatherUtil {

    static public class PullWeather{
        static ArrayList<Weather> parseWeather(String in,String cityName,String stateName) throws JSONException {

            ArrayList<Weather> weatherList = new ArrayList<>();
            JSONObject root = new JSONObject(in);

            if(root.has("error"))
            {
                JSONObject errorJSONObject = root.getJSONObject("error");

                return null;
            }
            else {
                JSONArray weatherJSONArray = root.getJSONArray("hourly_forecast");
                Log.d("outside", "checking");
                int maxTemp=0;
                int minTemp=1000;
                for (int i = 0; i < weatherJSONArray.length(); i++) {
                    JSONObject weatherJSONobject = weatherJSONArray.getJSONObject(i);
                    Weather weather = new Weather();
                    Log.d("inside", "checking");
                    String hour=weatherJSONobject.getJSONObject("FCTTIME").getString("hour");
                    String min =weatherJSONobject.getJSONObject("FCTTIME").getString("min");
                    String year =weatherJSONobject.getJSONObject("FCTTIME").getString("year");
                    String month =weatherJSONobject.getJSONObject("FCTTIME").getString("mon_padded");
                    String day =weatherJSONobject.getJSONObject("FCTTIME").getString("mday_padded");
                    String meridiem = "am";
                    int hour_int=Integer.parseInt(hour);
                    if(hour_int>12)
                    {
                        hour_int=hour_int-12;
                        hour =Integer.toString(hour_int);
                        meridiem="pm";
                    }
                    else if(hour_int==0)
                    {
                        hour_int=12;
                        hour =Integer.toString(hour_int);
                    }
                    weather.setPressure(weatherJSONobject.getJSONObject("mslp").getString("english"));
                    weather.setTemperature(weatherJSONobject.getJSONObject("temp").getString("english"));
                    weather.setTime(month+"/"+day+"/"+year+"-"+hour+":"+min+" "+meridiem);
                    weather.setDewpoint(weatherJSONobject.getJSONObject("dewpoint").getString("english"));
                    weather.setClouds(weatherJSONobject.getString("condition"));
                    weather.setIconUrl(weatherJSONobject.getString("icon_url"));
                    weather.setWindSpeed(weatherJSONobject.getJSONObject("wspd").getString("english"));
                    weather.setWindDirection(weatherJSONobject.getJSONObject("wdir").getString("degrees")+"°"+weatherJSONobject.getJSONObject("wdir").getString("dir"));
                    weather.setHumidity(weatherJSONobject.getString("humidity"));
                    weather.setFeelsLike(weatherJSONobject.getJSONObject("feelslike").getString("english"));
                    weather.setClimateType(weatherJSONobject.getString("wx"));
                    weather.setMaximumTemp("");
                    weather.setMinimumTemp("");
                    weather.setCityName(cityName);
                    weather.setStateName(stateName);
                    if(Integer.parseInt(weather.getTemperature())>maxTemp)
                    {
                        maxTemp=Integer.parseInt(weather.getTemperature());
                    }
                    else if(Integer.parseInt(weather.getTemperature())<minTemp){
                        minTemp=Integer.parseInt(weather.getTemperature());
                    }
                    Log.d("weather",weather.toString());
                    weatherList.add(weather);
                }
                for(int i=0;i<weatherList.size();i++)
                {
                    weatherList.get(i).setMaximumTemp(Integer.toString(maxTemp));
                    weatherList.get(i).setMinimumTemp(Integer.toString(minTemp));

                }
                return weatherList;
            }



        }
    }
}

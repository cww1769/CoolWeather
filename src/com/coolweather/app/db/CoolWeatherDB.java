package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.util.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CoolWeatherDB {

	public static final String DB_NAME = "cool_weather";
	public static final int VERSION = 2;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public void saveWeatherInfo(List<WeatherInfo> infolist) {
		if (infolist != null && infolist.size() > 0) {
			for (WeatherInfo info : infolist) {
				ContentValues values = new ContentValues();
				values.put("cityName", info.getCityName());
				values.put("high_tmp", info.getHigh_tmp());
				values.put("low_tmp", info.getLow_tmp());
				values.put("current_tmp", info.getCurrent_tmp());
				values.put("publishTime", info.getPublishTime());
				values.put("date", info.getDate());
				values.put("day_weather", info.getDay_weather());
				values.put("night_weather", info.getNight_weather());
				values.put("current_weather", info.getCurrent_weather());
				db.insert("weatherInfo", null, values);
			}
		}
	}

	/* get the weather info of the given city */
	public List<WeatherInfo> getWeatherInfo(String cityName) {
		List<WeatherInfo> infolist = new ArrayList<WeatherInfo>();

		Cursor cursor = db.query("weatherInfo", null, "cityName = ?",
				new String[] { cityName }, null, null, "date");

		if (cursor.moveToFirst()) {
			do {
				WeatherInfo info = new WeatherInfo();
				info.setCityName(cityName);
				info.setHigh_tmp(cursor.getString(cursor
						.getColumnIndex("high_tmp")));
				info.setLow_tmp(cursor.getString(cursor
						.getColumnIndex("low_tmp")));
				info.setCurrent_tmp(cursor.getString(cursor
						.getColumnIndex("current_tmp")));
				info.setPublishTime(cursor.getString(cursor
						.getColumnIndex("publishTime")));
				info.setDate(cursor.getString(cursor.getColumnIndex("date")));
				info.setDay_weather(cursor.getString(cursor
						.getColumnIndex("day_weather")));
				info.setNight_weather(cursor.getString(cursor
						.getColumnIndex("night_weather")));
				info.setCurrent_weather(cursor.getString(cursor
						.getColumnIndex("current_weather")));
				infolist.add(info);
			} while (cursor.moveToNext());
		}
		return infolist;
	}

	/* get the saved city name which is chinsese */
	public List<String> getSavedCity() {
		List<String> savedCities = new ArrayList<String>();
		Cursor cursor = db.query("savedCity", null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				String city = cursor.getString(cursor
						.getColumnIndex("cityName"));
				int priority = cursor.getInt(cursor.getColumnIndex("priority"));
				//Log.i("vivian", "city=" + city + "priority=" + priority);
				savedCities.add(city);
			} while (cursor.moveToNext());
		}
		return savedCities;
	}

	public void saveSelectedCities(List<String> savedCities) {
		if (savedCities != null && !savedCities.isEmpty()) {
			for (int i = 0; i < savedCities.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("cityName", savedCities.get(i));
				values.put("priority", i);
				db.insert("savedCity", null, values);
			}
		}
	}
	
	public void saveSelectedCity(String savedCity) {
		if (Utility.isStringEmpty(savedCity)) {			
				ContentValues values = new ContentValues();
				values.put("cityName", savedCity);
				values.put("priority", 10);
				db.insert("savedCity", null, values);			
		}
	}

	public void deleteCites() {
		db.delete("savedCity", null, null);
	}

	public void deleteCites(String cityName) {
		db.delete("savedCity", "cityName = ?", new String[] { cityName });
	}

	public void deleteWeatherInfo(String cityName) {
		db.delete("weatherInfo", "cityName = ?", new String[] { cityName });
	}
	
	
	public static final String CREATE_PROVINCE = "create table province (provinceCode text primary key,"
			+ "provinceName text)";
	
	public static final String CREATE_CITY = "create table city (cityCode text primary key,"
			+ "cityName text, "  + "provinceCode text)";
	
	public List<City> getCities(String provinceCode){
		List<City> cities = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "provinceCode = ?",	new String[] { provinceCode }, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
				city.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
				cities.add(city);
				} while (cursor.moveToNext());
		}
		return cities;
	}
	
	public void saveCities(List<City> cities){
		if (!Utility.isListEmpty(cities)){
			for (City city: cities){
				ContentValues values = new ContentValues();
				values.put("cityCode", city.getCityCode());				
				values.put("cityName", city.getCityName());
				values.put("provinceCode", city.getProvinceCode());
				db.insert("city", null, values);
			}
		}
	}
	
	public List<Province> getProvinces(){
		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db.query("province", null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
				provinces.add(province);
				} while (cursor.moveToNext());
		}
		return provinces;
	}
	
	public void saveProvinces(List<Province> provinces){
		if (!Utility.isListEmpty(provinces)){
			for (Province province: provinces){
				ContentValues values = new ContentValues();
				values.put("provinceName", province.getProvinceName());
				values.put("provinceCode", province.getProvinceCode());
				db.insert("province", null, values);
			}
		}
	}
}
